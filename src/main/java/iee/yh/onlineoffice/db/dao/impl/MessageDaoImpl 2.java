package iee.yh.onlineoffice.db.dao.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONObject;
import iee.yh.onlineoffice.db.dao.MessageDao;
import iee.yh.onlineoffice.db.entity.MessageEntity;
import iee.yh.onlineoffice.db.entity.MessageRefEntity;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperationContext;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author yanghan
 * @date 2022/5/6
 */
@Repository
public class MessageDaoImpl implements MessageDao {
    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 插入消息
     * @param messageEntity 消息实体类
     * @return
     */
    @Override
    public String insert(MessageEntity messageEntity) {
        Date sendTime = messageEntity.getSendTime();
        /**
         * mongodb时间为格林尼治时间，北京为东八区，需要偏移8个小时
         */
        sendTime = DateUtil.offset(sendTime, DateField.HOUR, 8);
        messageEntity.setSendTime(sendTime);
        messageEntity = mongoTemplate.save(messageEntity);
        return messageEntity.get_id();
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
        JSONObject json = new JSONObject();
        json.put("$toString","$_id");
        Aggregation aggregation = Aggregation.newAggregation(
                new AggregationOperation() {
                    @Override
                    public Document toDocument(AggregationOperationContext aoc) {
                        return new Document("$addFields",new Document("id",new Document("$toString","$_id")));
                    }
                },
                Aggregation.lookup("message_ref","id","messageId","ref"),
                Aggregation.match(Criteria.where("ref.receiverId").is(userId)),
                Aggregation.sort(Sort.by(Sort.Direction.DESC,"sendTime")),
                Aggregation.skip(start),
                Aggregation.limit(length)
        );
        AggregationResults<HashMap> results = mongoTemplate.aggregate(aggregation, "message", HashMap.class);
        List<HashMap> mappedResults = results.getMappedResults();
        //时间转换
        for (HashMap mappedResult : mappedResults) {
            //一对多的关系（一个消息，多个接收者）
            List<MessageRefEntity> refList = (List<MessageRefEntity>)mappedResult.get("ref");
            MessageRefEntity messageRefEntity = refList.get(0);
            Boolean readFlag = messageRefEntity.getReadFlag();
            String refid = messageRefEntity.get_id();
            mappedResult.put("readFlag",readFlag);
            mappedResult.put("refId",refid);
            mappedResult.remove("ref");
            mappedResult.remove("_id");
            Date sendTime = (Date) mappedResult.get("sendTime");
            /**
             * 时间修改逻辑：
             *      格林尼治时间修改为北京时间
             *      判断 消息发送日期 与 当前日期 是否相同,如果相同则返回为：具体时间 eg：13:14
             *                             如果不相同则返回为 年月日 eg:2022/01/01
             */
            sendTime = DateUtil.offset(sendTime, DateField.HOUR, -8);
            if (DateUtil.today().equals(DateUtil.date(sendTime).toDateStr())){
                mappedResult.put("sendTime",DateUtil.format(sendTime,"HH:mm"));
            }else
                mappedResult.put("sendTime",DateUtil.format(sendTime,"yyyy/MM/dd"));
        }
        return mappedResults;
    }

    /**
     * 按照消息id去查询消息
     * @param id
     * @return
     */
    @Override
    public HashMap searchMessageById(String id) {
        HashMap message = mongoTemplate.findById(id, HashMap.class, "message");
        Date sendTime = (Date) message.get("sendTime");
        sendTime = DateUtil.offset(sendTime,DateField.HOUR,-8);
        message.replace("sendTime",DateUtil.format(sendTime,"yyyy-MM-dd HH:mm"));
        return message;
    }
}
