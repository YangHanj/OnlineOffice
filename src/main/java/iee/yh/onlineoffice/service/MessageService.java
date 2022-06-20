package iee.yh.onlineoffice.service;

import iee.yh.onlineoffice.db.entity.MessageEntity;
import iee.yh.onlineoffice.db.entity.MessageRefEntity;

import java.util.HashMap;
import java.util.List;

/**
 * @author yanghan
 * @date 2022/5/6
 */
public interface MessageService {
    /**
     * 插入消息
     * @param messageEntity 消息实体类
     * @return
     */
    String insertMessage(MessageEntity messageEntity);

    /**
     * 插入ref消息
     * @param messageRefEntity
     * @return
     */
    String insertRef(MessageRefEntity messageRefEntity);

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
     * 分页查询
     * @param userId 用户id
     * @param start 起始页
     * @param length
     * @return
     */
    List<HashMap> searchMessageByPage(Integer userId, Long start, Integer length);

    /**
     * 按照消息id去查询消息
     * @param id
     * @return
     */
    HashMap searchMessageById(String id);

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
