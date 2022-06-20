package iee.yh.onlineoffice.db.dao.impl;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import iee.yh.onlineoffice.db.dao.MessageRefDao;
import iee.yh.onlineoffice.db.entity.MessageRefEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

/**
 * @author yanghan
 * @date 2022/5/6
 */
@Repository
public class MessageRefDaoImpl implements MessageRefDao {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 插入ref消息
     * @param messageRefEntity
     * @return
     */
    @Override
    public String insert(MessageRefEntity messageRefEntity) {
        messageRefEntity = mongoTemplate.save(messageRefEntity);
        return messageRefEntity.get_id();
    }

    /**
     * 查询未读消息
     * @param userId 用户ID
     * @return
     */
    @Override
    public Long searchUnreadCount(Integer userId) {
        long count = mongoTemplate.count(
                    new Query().addCriteria(Criteria.where("readFlag").is(false).and("receiverId").is(userId)),
                    MessageRefEntity.class
        );
        return count;
    }

    /**
     * 查询用户新接收消息数量
     * @param userId 用户ID
     * @return
     */
    @Override
    public Long searchLastCount(Integer userId) {
        Update update = new Update();
        update.set("lastFlag",false);
        /**
         * @param1 修改的条件
         * @param2 需要修改的值
         * @param3 修改的集合名称
         */
        UpdateResult updateResult = mongoTemplate.updateMulti(
                new Query().addCriteria(Criteria.where("lastFlag").is(true).and("receiverId").is(userId)),
                update,
                "message_ref"
        );
        if (updateResult != null)
            return updateResult.getMatchedCount();
        else return (long)0;
    }

    /**
     * 将消息从 未读状态 修改为 已读状态
     * @param id 集合记录主键
     * @return
     */
    @Override
    public Long updateUnreadMessage(String id) {
        Update update = new Update();
        update.set("readFlag",true);
        UpdateResult updateResult = mongoTemplate.updateFirst(
                new Query().addCriteria(Criteria.where("_id").is(id)),
                update,
                "message_ref"
        );
        if (updateResult != null)
            return updateResult.getMatchedCount();
        else return (long)0;
    }

    /**
     * 删除消息
     * @param id 集合记录主键
     * @return
     */
    @Override
    public Long deleteMessageRefById(String id) {
        DeleteResult remove = mongoTemplate.remove(
                new Query(Criteria.where("_id").is(id)),
                "message_ref"
        );
        if (remove != null)
            return remove.getDeletedCount();
        else return (long)0;
    }

    /**
     * 删除员工的消息
     * @param userId 用户ID
     * @return
     */
    @Override
    public Long deleteUserMessageRef(Integer userId) {
        DeleteResult remove = mongoTemplate.remove(
                new Query(Criteria.where("receiverId").is(userId)),
                "messagr_ref"
        );
        if (remove != null)
            return remove.getDeletedCount();
        else return (long)0;
    }
}
