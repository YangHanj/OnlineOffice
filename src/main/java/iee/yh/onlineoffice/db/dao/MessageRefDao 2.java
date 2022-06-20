package iee.yh.onlineoffice.db.dao;

import iee.yh.onlineoffice.db.entity.MessageRefEntity;

/**
 * @author yanghan
 * @date 2022/5/6
 */
public interface MessageRefDao {

    /**
     * 插入ref消息
     * @param messageRefEntity
     * @return
     */
    String insert(MessageRefEntity messageRefEntity);

    /**
     * 查询未读消息
     * @param userId 用户ID
     * @return
     */
    Long searchUnreadCount(Integer userId);

    /**
     * 查询用户新接收消息数量
     * @param userId 用户ID
     * @return
     */
    Long searchLastCount(Integer userId);

    /**
     * 将消息从 未读状态 修改为 已读状态
     * @param id 集合记录主键
     * @return
     */
    Long updateUnreadMessage(String id);

    /**
     * 删除消息
     * @param id 集合记录主键
     * @return
     */
    Long deleteMessageRefById(String id);

    /**
     * 删除员工的消息
     * @param userId 用户ID
     * @return
     */
    Long deleteUserMessageRef(Integer userId);
}
