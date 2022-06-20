package iee.yh.onlineoffice.db.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

/**
 * MongoDB存储发送消息数据结构
 * @author yanghan
 * @date 2022/5/6
 */
@Document(collection = "message")
public class MessageEntity implements Serializable {
    @Id
    private String _id;  //自动生成主键值
    @Indexed(unique = true)
    private String uuid;  //UUID值，设置唯一索引，防止消息被重复消费
    @Indexed
    private Integer senderId; //发送者ID
    //发送者头像
    private String senderPhoto = "https://onlineofficeminiapp.oss-cn-beijing.aliyuncs.com/img/admin.png";
    private String senderName; //发送者名称
    private String msg;  // 消息正文
    @Indexed
    private Date sendTime; //发送时间

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Integer getSenderId() {
        return senderId;
    }

    public void setSenderId(Integer senderId) {
        this.senderId = senderId;
    }

    public String getSenderPhoto() {
        return senderPhoto;
    }

    public void setSenderPhoto(String senderPhoto) {
        this.senderPhoto = senderPhoto;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }
}

