package iee.yh.onlineoffice.service;

import iee.yh.onlineoffice.db.entity.MeetingEntity;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author yanghan
 * @date 2022/5/11
 */
public interface MeetingService {
    /**
     * 添加会议记录
     * @param meetingEntity 会议属性实体类
     */
    void insertMeeting(MeetingEntity meetingEntity);

    /**
     * 分页查询会议记录
     * @param param 0：员工id 1：开始记录 2：单页个数
     * @return
     */
    ArrayList<HashMap> searchMyMeetingListPage(HashMap param);

    /**
     * 查询会议详情
     * @param id
     * @return
     */
    HashMap searchMeetingById(int id);

    /**
     * 更新会议内容
     * @param map
     * @return
     */
    Integer updateMeetingInfo(HashMap map);

    /**
     * 删除会议（只能删除审批通过的）
     * @param id 会议id
     */
    void DeleteMeetingService(int id);

    /**
     * 查询待审批与已审批的会议列表
     * @param param 查询类型 页数 每页记录数
     * @return
     */
    ArrayList<HashMap> searchUserTaskListByPage(HashMap param);

    /**
     * 审批会议
     * @param param
     * @return
     */
    Integer approvalMeeting(HashMap param);
}
