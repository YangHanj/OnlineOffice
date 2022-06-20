package iee.yh.onlineoffice.controller;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import iee.yh.onlineoffice.common.exception.CommonException;
import iee.yh.onlineoffice.common.result.R;
import iee.yh.onlineoffice.common.util.JwtUtils;
import iee.yh.onlineoffice.common.vo.*;
import iee.yh.onlineoffice.db.entity.MeetingEntity;
import iee.yh.onlineoffice.service.MeetingService;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * @author yanghan
 * @date 2022/5/11
 */
@RestController
@RequestMapping("/meeting")
public class MeetingController {
    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private MeetingService meetingService;

    @PostMapping("/searchMyMeetingListByPage")
    @ApiOperation("会议记录查询")
    public R SearchMyMeetingListPage(@RequestHeader("token")String token,
                                     @Valid @RequestBody SearchMyMeetingListPageFormVO searchMyMeetingListPageFromVO){
        int userId = jwtUtils.getUserId(token);
        Integer page = searchMyMeetingListPageFromVO.getPage();
        Integer length = searchMyMeetingListPageFromVO.getLength();
        //起始记录
        long start = (page - 1) * length;
        HashMap map = new HashMap();
        map.put("userId",userId);
        map.put("start",start);
        map.put("length",length);
        ArrayList<HashMap> list = meetingService.searchMyMeetingListPage(map);
        return R.ok().put("result",list);
    }

    @PostMapping("/insertMeeting")
    @ApiOperation("添加会议")
    @RequiresPermissions(value = {"ROOT","MEETING:INSERT"},logical = Logical.OR)
    public R insertMeeting(@Valid @RequestBody InsertMeetingFormVO insertMeetingFormVO,
                           @RequestHeader("token")String token){
        if (insertMeetingFormVO.getType() == 2
                &&
            (insertMeetingFormVO.getPlace() == null ||insertMeetingFormVO.getPlace().length() == 0)){
            throw new CommonException("线下会议不能为空");
        }
        DateTime d1 = DateUtil.parse(insertMeetingFormVO.getDate()+" "+insertMeetingFormVO.getStart()+":00");
        DateTime d2 = DateUtil.parse(insertMeetingFormVO.getDate()+" "+insertMeetingFormVO.getEnd()+":00");

        if (d2.isBeforeOrEquals(d1)){
            throw new CommonException("会议结束时间不得早于开始时间");
        }
        if (!JSONUtil.isJsonArray(insertMeetingFormVO.getMembers())){
            throw new CommonException("members必须是JSON数组");
        }
        MeetingEntity meetingEntity = new MeetingEntity();
        meetingEntity.setUuid(UUID.randomUUID().toString());
        meetingEntity.setTitle(insertMeetingFormVO.getTitle());
        meetingEntity.setCreatorId((long)jwtUtils.getUserId(token));
        meetingEntity.setPlace(insertMeetingFormVO.getPlace());
        meetingEntity.setStart(insertMeetingFormVO.getStart()+":00");
        meetingEntity.setEnd(insertMeetingFormVO.getEnd()+":00");
        meetingEntity.setType((short)insertMeetingFormVO.getType());
        meetingEntity.setMembers(insertMeetingFormVO.getMembers());
        meetingEntity.setDesc(insertMeetingFormVO.getDesc());
        meetingEntity.setStatus((short) 1);
        meetingEntity.setDate(insertMeetingFormVO.getDate());
        meetingEntity.setCreateTime(DateUtil.date());
        meetingService.insertMeeting(meetingEntity);
        return R.ok().put("result","success");
    }

    @PostMapping("/searchMeetingById")
    @ApiOperation("根据ID查询会议")
    @RequiresPermissions(value = {"ROOT","MEETING:SELECT"},logical = Logical.OR)
    public R searchMeetingById(@Valid @RequestBody SearchMeetingByIdFromVO searchMeetingByIdFromVO){
        HashMap map = meetingService.searchMeetingById(searchMeetingByIdFromVO.getId());
        return R.ok().put("result",map);
    }

    @PostMapping("/updateMeetingInfo")
    @ApiOperation("更新会议")
    @RequiresPermissions(value = {"ROOT","MEETING:UPDATE"},logical = Logical.OR)
    public R updateMeetingController(@Valid @RequestBody UpdateMeetingInfoFormVO updateMeetingInfoFormVO){
        if (updateMeetingInfoFormVO.getType() == 2
                &&
                (updateMeetingInfoFormVO.getPlace() == null ||updateMeetingInfoFormVO.getPlace().length() == 0)){
            throw new CommonException("线下会议不能为空");
        }
        DateTime d1 = DateUtil.parse(updateMeetingInfoFormVO.getDate()+" "+updateMeetingInfoFormVO.getStart()+":00");
        DateTime d2 = DateUtil.parse(updateMeetingInfoFormVO.getDate()+" "+updateMeetingInfoFormVO.getEnd()+":00");

        if (d2.isBeforeOrEquals(d1)){
            throw new CommonException("会议结束时间不得早于开始时间");
        }
        if (!JSONUtil.isJsonArray(updateMeetingInfoFormVO.getMembers())){
            throw new CommonException("members必须是JSON数组");
        }
        HashMap map = new HashMap();
        map.put("title",updateMeetingInfoFormVO.getTitle());
        map.put("date",updateMeetingInfoFormVO.getDate());
        map.put("place",updateMeetingInfoFormVO.getPlace());
        map.put("start",updateMeetingInfoFormVO.getStart()+":00");
        map.put("end",updateMeetingInfoFormVO.getEnd()+":00");
        map.put("type",updateMeetingInfoFormVO.getType());
        map.put("members",updateMeetingInfoFormVO.getMembers());
        map.put("desc",updateMeetingInfoFormVO.getDesc());
        map.put("id",updateMeetingInfoFormVO.getId());
        map.put("instanceId",updateMeetingInfoFormVO.getInstanceId());
        map.put("status",1);
        meetingService.updateMeetingInfo(map);
        return R.ok().put("result","success");
    }

    @ApiOperation("删除会议")
    @PostMapping("/deleteMeetingById")
    @RequiresPermissions(value = {"ROOT","MEETING:DELETE"},logical = Logical.OR)
    public R deleteMeetingById(@Valid @RequestBody DeleteMeetingByIdFormVO deleteMeetingByIdFormVO){
        meetingService.DeleteMeetingService(deleteMeetingByIdFormVO.getId());
        return R.ok().put("result","success");
    }

    @PostMapping("/searchUserTaskListByPage")
    @ApiOperation("查询待审批与已审批会议")
    @RequiresPermissions(value = {"WORKFLOW:APPROVAL"})
    public R SearchUserTaskListByPage(@Valid @RequestBody SearchApprovalMeetingFormVO searchApprovalMeetingFormVO){
        HashMap param = new HashMap();
        long start = (searchApprovalMeetingFormVO.getPage() - 1) * searchApprovalMeetingFormVO.getLength();
        param.put("type",searchApprovalMeetingFormVO.getType().equals("待审批")?"1":"3");
        param.put("start",start);
        param.put("length",searchApprovalMeetingFormVO.getLength());
        ArrayList<HashMap> list = meetingService.searchUserTaskListByPage(param);
        return R.ok().put("result",list);
    }

    @PostMapping("/approvalMeeting")
    @ApiOperation("会议审批")
    @RequiresPermissions(value = {"WORKFLOW:APPROVAL"})
    public R approvalMeeting(@Valid @RequestBody approvalMeetingFormVO approvalMeetingFormVO,
                             @RequestHeader("token") String token){
        int userId = jwtUtils.getUserId(token);
        HashMap param = new HashMap();
        param.put("approval",approvalMeetingFormVO.getApproval());
        param.put("taskId",approvalMeetingFormVO.getTaskId());
        param.put("processType",approvalMeetingFormVO.getProcessType());
        param.put("userId",userId);
        meetingService.approvalMeeting(param);
        return R.ok().put("result","success");
    }
}
