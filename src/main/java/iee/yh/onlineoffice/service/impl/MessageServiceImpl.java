package iee.yh.onlineoffice.service.impl;

import iee.yh.onlineoffice.db.dao.MessageDao;
import iee.yh.onlineoffice.db.dao.MessageRefDao;
import iee.yh.onlineoffice.db.entity.MessageEntity;
import iee.yh.onlineoffice.db.entity.MessageRefEntity;
import iee.yh.onlineoffice.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

/**
 * @author yanghan
 * @date 2022/5/6
 */
@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageDao messageDao;
    @Autowired
    private MessageRefDao messageRefDao;

    /**
     * 插入消息
     * @param messageEntity 消息实体类
     * @return
     */
    @Override
    public String insertMessage(MessageEntity messageEntity) {
        String insert = messageDao.insert(messageEntity);
        return insert;
    }

    /**
     * 插入ref消息
     * @param messageRefEntity
     * @return
     */
    @Override
    public String insertRef(MessageRefEntity messageRefEntity) {
        String insert = messageRefDao.insert(messageRefEntity);
        return insert;
    }

    /**
     * 查询未读消息
     * @param userId 用户ID
     * @return
     */
    @Override
    public Long searchUnreadCount(Integer userId) {
        Long aLong = messageRefDao.searchUnreadCount(userId);
        return aLong;
    }

    /**
     * 查询用户新接收消息数量
     * @param userId 用户ID
     * @return
     */
    @Override
    public Long searchLastCount(Integer userId) {
        Long aLong = messageRefDao.searchLastCount(userId);
        return aLong;
    }

    /**
     * 分页查询
     * @param userId 用户id
     * @param start 起始页
     * @param length
     * @return
     */
    @Override
    public List<HashMap> searchMessageByPage(Integer userId, Long start, Integer length) {
        List<HashMap> hashMaps = messageDao.searchMessageByPage(userId, start, length);
        return hashMaps;
    }

    /**
     * 按照消息id去查询消息
     * @param id
     * @return
     */
    @Override
    public HashMap searchMessageById(String id) {
        HashMap map = messageDao.searchMessageById(id);
        return map;
    }

    /**
     * 将消息从 未读状态 修改为 已读状态
     * @param id 集合记录主键
     * @return
     */
    @Override
    public Long updateUnreadMessage(String id) {
        Long aLong = messageRefDao.updateUnreadMessage(id);
        return aLong;
    }

    /**
     * 删除消息
     * @param id 集合记录主键
     * @return
     */
    @Override
    public Long deleteMessageRefById(String id) {
        Long aLong = messageRefDao.deleteMessageRefById(id);
        return aLong;
    }

    /**
     * 删除员工的消息
     * @param userId 用户ID
     * @return
     */
    @Override
    public Long deleteUserMessageRef(Integer userId) {
        Long aLong = messageRefDao.deleteUserMessageRef(userId);
        return aLong;
    }
}
