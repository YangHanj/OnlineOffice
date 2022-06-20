package iee.yh.onlineoffice.db.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * 使用群发消息的时候，该实体类用来记录接收人和已读状态
 * @author yanghan
 * @date 2022/5/6
 */
@Document(collection = "message_ref")
public class MessageRefEntity implements Serializable {
    @Id
    private String _id;
    @Indexed
    private String messageId;
    @Indexed
    private Integer receiverId;
    @Indexed
    private Boolean readFlag;
    @Indexed
    private Boolean lastFlag;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public Integer getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Integer receiverId) {
        this.receiverId = receiverId;
    }

    public Boolean getReadFlag() {
        return readFlag;
    }

    public void setReadFlag(Boolean readFlag) {
        this.readFlag = readFlag;
    }

    public Boolean getLastFlag() {
        return lastFlag;
    }

    public void setLastFlag(Boolean lastFlag) {
        this.lastFlag = lastFlag;
    }
}
