package iee.yh.onlineoffice.db.dao;

import iee.yh.onlineoffice.db.entity.MeetingEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.shiro.crypto.hash.Hash;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 会议表
 * 
 * @author yanghan
 * @email yanghan1214@163.com
 * @date 2022-04-20 16:15:12
 */
@Mapper
public interface MeetingDao extends BaseMapper<MeetingEntity> {
    /**
     * 分页查询会议记录
     * @param param 0：员工id 1：开始记录 2：单页个数
     * @return
     */
    ArrayList<HashMap> searchMyMeetingListPage(HashMap param);

    /**
     * 查看参加会议的人是否在同一个部门
     * @param uuid
     * @return
     */
    boolean searchMeetingMembersInSameDept(String uuid);

    /**
     * 更新工作流
     * @param param
     */
    Integer updateMeetingInstanceId(HashMap param);

    HashMap searchMeetingById(int id);

    ArrayList<HashMap> searchMeetingMembers(int id);

    /**
     *  修改会议记录
     * @param map
     * @return
     */
    Integer updateMeetingInfo(HashMap map);

    /**
     * 查询待审批与已审批的会议列表
     * @param param 查询类型 页数 每页记录数
     * @return
     */
    ArrayList<HashMap> searchUserTaskListByPage(HashMap param);
}
