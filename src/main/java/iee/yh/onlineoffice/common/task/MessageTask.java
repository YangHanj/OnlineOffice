package iee.yh.onlineoffice.common.task;

import com.rabbitmq.client.*;
import iee.yh.onlineoffice.common.exception.CommonException;
import iee.yh.onlineoffice.db.entity.MessageEntity;
import iee.yh.onlineoffice.db.entity.MessageRefEntity;
import iee.yh.onlineoffice.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 采用异步线程执行rabbitmq的同步收发
 * @author yanghan
 * @date 2022/5/8
 */
@Slf4j
@Component
public class MessageTask implements Serializable {
    @Autowired
    private ConnectionFactory connectionFactory;
    @Autowired
    private MessageService messageService;

    /**
     * 发送消息
     * @param topic 队列名称
     * @param messageEntity 消息内容
     */
    public void send(String topic, MessageEntity messageEntity){
        String messaheId = messageService.insertMessage(messageEntity);
        try (
                //创建rabbitmq的连接
                Connection connection = connectionFactory.newConnection();
                Channel channel = connection.createChannel()
        ){
            channel.queueDeclare(topic,true,false,false,null);
            //构建消息头
            HashMap map = new HashMap();
            map.put("messageId",messaheId);
            AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder().headers(map).build();
            //发送消息
            channel.basicPublish("",topic,properties,messageEntity.getMsg().getBytes());

            log.debug("发新消息成功");
        }catch (Exception e){
            log.error("发送消息异常",e);
            throw new CommonException("MQ发送消息异常");
        }
    }

    /**
     * 异步发送消息
     * @param topic
     * @param messageEntity
     */
    @Async
    public void sendAsync(String topic, MessageEntity messageEntity){
        send(topic,messageEntity);
    }

    /**
     * 接收消息
     * @param topic 队列名称(receiverID)
     * @return
     */
    public int receive(String topic){
        int i = 0;
        try(    //创建rabbitmq的连接
                Connection connection = connectionFactory.newConnection();
                Channel channel = connection.createChannel()
        ){
            channel.queueDeclare(topic,true,false,false,null);
            for (;;){
                //接收到消息之后，写入ref集合，在进行应答（手动应答）
                GetResponse getResponse = channel.basicGet(topic, false);
                if (getResponse != null){
                    //获取消息头
                    AMQP.BasicProperties props = getResponse.getProps();
                    Map<String, Object> headers = props.getHeaders();
                    String messageId = headers.get("messageId").toString();
                    //获取消息体
                    byte[] body = getResponse.getBody();
                    String message = new String(body);
                    log.debug("接收到消息");
                    //构建ref
                    MessageRefEntity messageRefEntity = new MessageRefEntity();
                    messageRefEntity.setMessageId(messageId);
                    messageRefEntity.setReceiverId(Integer.parseInt(topic));
                    messageRefEntity.setLastFlag(true); //默认最后一条消息
                    messageRefEntity.setReadFlag(false); //默认未读
                    //存入mongodb
                    messageService.insertRef(messageRefEntity);
                    //手动应答
                    long deliveryTag = getResponse.getEnvelope().getDeliveryTag();
                    channel.basicAck(deliveryTag,false);
                    i++;
                }else break;
            }
        }catch (Exception e){
            log.error("接收消息异常",e);
            throw new CommonException("MQ接收消息异常");
        }
        return i;
    }

    @Async
    public int receiveAsync(String topic){
        return receive(topic);
    }

    /**
     * 删除消息队列
     * @param topic 队列名称
     */
    public void deleteQueue(String topic){
        try(//创建rabbitmq的连接
            Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel()
        ){
            channel.queueDelete(topic);
            log.debug("删除队列成功");
        }catch (Exception e){
            log.error("删除队列失败",e);
            throw new CommonException("删除队列失败");
        }
    }

    @Async
    public void deleteQueueAsync(String topic){
        deleteQueue(topic);
    }
}
