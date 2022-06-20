package iee.yh.onlineoffice.db.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import iee.yh.onlineoffice.db.entity.ApprovalEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;

/**
 * @author yanghan
 * @date 2022/5/14
 */
@Mapper
public interface ApprovalDao extends BaseMapper<ApprovalEntity> {
    /**
     * 查询会议审批人信息
     * @param taskId 会议任务号
     * @return
     */
    HashMap searchApprovalInfo(String taskId);
}
