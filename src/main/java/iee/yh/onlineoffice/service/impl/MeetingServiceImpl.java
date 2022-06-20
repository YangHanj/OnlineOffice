package iee.yh.onlineoffice.service.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import iee.yh.onlineoffice.common.exception.CommonException;
import iee.yh.onlineoffice.db.dao.ApprovalDao;
import iee.yh.onlineoffice.db.dao.MeetingDao;
import iee.yh.onlineoffice.db.dao.UserDao;
import iee.yh.onlineoffice.db.entity.ApprovalEntity;
import iee.yh.onlineoffice.db.entity.MeetingEntity;
import iee.yh.onlineoffice.db.entity.UserEntity;
import iee.yh.onlineoffice.service.MeetingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

/**
 * @author yanghan
 * @date 2022/5/11
 */
@Slf4j
@Service
public class MeetingServiceImpl implements MeetingService {
    @Autowired
    private MeetingDao meetingDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private ApprovalDao approvalDao;

    @Value("${yang.recieveNotify}")
    private String recieveNotify;

    /**
     * 添加会议记录
     * @param meetingEntity 会议属性实体类
     */
    @Override
    @Transactional
    public void insertMeeting(MeetingEntity meetingEntity) {
        int insert = meetingDao.insert(meetingEntity);
        if (insert != 1)
            throw new CommonException("会议创建失败");
        //TODO 工作审批
        startMeetingWorkflow(meetingEntity.getUuid(),
                             meetingEntity.getCreatorId().intValue(),
                             meetingEntity.getDate(),
                             meetingEntity.getStart());
    }

    /**
     * 分页查询会议记录
     * @param param 0：员工id 1：开始记录 2：单页个数
     * @return
     */
    @Override
    public ArrayList<HashMap> searchMyMeetingListPage(HashMap param) {
        ArrayList<HashMap> list = meetingDao.searchMyMeetingListPage(param);
        String lastdate = null;
        ArrayList<HashMap> resultList = new ArrayList<>();
        HashMap resultmap = null;
        JSONArray jsonArray = null;
        for (HashMap map : list) {
            String date = map.get("date").toString();
            //按会议的日期对查询出来的会议进行分组
            if (!date.equals(lastdate)){
                lastdate = date;
                resultmap = new HashMap();
                resultmap.put("date",date);
                jsonArray = new JSONArray();
                resultmap.put("list",jsonArray);
                resultList.add(resultmap);
            }
            jsonArray.put(map);
        }
        return resultList;
    }

    /**
     * 查询会议详情
     * @param id
     * @return
     */
    @Override
    public HashMap searchMeetingById(int id) {
        HashMap map = meetingDao.searchMeetingById(id);
        //查询参会人
        ArrayList<HashMap> list = meetingDao.searchMeetingMembers(id);
        map.put("members",list);
        return map;
    }

    @Override
    public Integer updateMeetingInfo(HashMap map) {
        Integer integer = meetingDao.updateMeetingInfo(map);
        if (!integer.equals(1))
            throw new CommonException("修改会议记录失败!");
        return integer;
    }

    /**
     *  删除会议（只能删除审批通过的）
     * @param id 会议id
     */
    @Override
    public void DeleteMeetingService(int id) {
        //获取会议详情
        HashMap map = meetingDao.searchMeetingById(id);
//        String uuid = map.get("uuid").toString();
//        String instanceId = map.get("instanceId").toString();
        DateTime date = DateUtil.parse(map.get("date") + " " + map.get("start") + ":00");
        DateTime now = DateUtil.date();
        //会议开始前20分钟，不能删除会议
        if (now.isAfterOrEquals(date.offset(DateField.MINUTE,-20))){
            throw new CommonException("距离会议开始不足20分钟，不能删除会议");
        };
        int delete = meetingDao.delete(new QueryWrapper<MeetingEntity>().eq("id", id).eq("status", 3));
        if (delete != 1){
            throw new CommonException("删除会议失败");
        }
    }

    /**
     * 添加会议
     */
    private void startMeetingWorkflow(String uuid,int creatorId,String date,String start){
        //获取会议创建者信息
        HashMap info = userDao.searchUserInfo(creatorId);
        //创建者角色
        String[] roles = info.get("roles").toString().split(",");

//            //创建人不是总经理，则查询该创建者对应的部门经理
//            int ManagerId = userDao.searchDeptManagerId(creatorId);
//            //查询总经理的
//            int bossId = userDao.searchGmId();
//            //查看参加该会议的人是否为同一部门
//            boolean b = meetingDao.searchMeetingMembersInSameDept(uuid);
            //*****模拟生成一个instanceId*****
            String instanceId = UUID.randomUUID().toString().replace("-","");
            HashMap param = new HashMap();
            param.put("uuid",uuid);
            param.put("instanceId",instanceId);
            Integer integer = meetingDao.updateMeetingInstanceId(param);
            if (!integer.equals(1)){
                throw new CommonException("创建会议失败,保存数据流失败");
            }

    }

    @Override
    public ArrayList<HashMap> searchUserTaskListByPage(HashMap param) {
        ArrayList<HashMap> list = meetingDao.searchUserTaskListByPage(param);
        String type = param.get("type").toString();
          for (HashMap map : list) {
            map.put("processType","meeting");
            //查询参会人部门
            ArrayList<Integer> arrayList = JSONUtil.parseArray(map.get("members")).toList(Integer.class);
            ArrayList<String> deptName = new ArrayList<>();
            for (Integer integer : arrayList) {
                HashMap map1 = userDao.searchDeptNameByUserId(integer);
                if (!deptName.contains(map1.get("dept_name").toString()))
                    deptName.add(map1.get("dept_name").toString());
            }
            map.put("deptname",deptName);
            //判断是否为同一个部门
            if (deptName.size() == 1)
                map.put("sameDept",1);
            else
                map.put("sameDept",2);
            if (type.equals("3")){
                //查询审批人
                HashMap map_1 = approvalDao.searchApprovalInfo(map.get("taskId").toString());
                map.put("approval_name",map_1.get("name"));
                map.put("approval_photo",map_1.get("photo"));
                map.put("result","同意");
            }
        }
        return list;
    }

    /**
     * 审批会议
     * @param param
     * @return
     */
    @Override
    @Transactional
    public Integer approvalMeeting(HashMap param) {
        String processType = param.get("processType").toString();
        if (!processType.equals("meeting")){
            throw new CommonException("该事件非会议事件!");
        }
        String instanceId = param.get("taskId").toString();
        String approval = param.get("approval").toString();
        int code = approval.equals("同意") ? 3 : 2;
        int update = meetingDao.update(null, new UpdateWrapper<MeetingEntity>().eq("instance_id", instanceId).set("status", code));
        ApprovalEntity approvalEntity = new ApprovalEntity();
        approvalEntity.setMeetingId(instanceId);
        approvalEntity.setUserId(Integer.parseInt(param.get("userId").toString()));
        int insert = approvalDao.insert(approvalEntity);
        if (update == 1 && insert == 1)
            //TODO:审批结束，给所有参会者发送邮件以及小程序消息
            //TODO:EmailTask MessageTask
            return update;
        else throw new CommonException("审批失败!");
    }
}
