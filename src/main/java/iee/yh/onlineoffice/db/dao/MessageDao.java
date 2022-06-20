package iee.yh.onlineoffice.db.dao;

import iee.yh.onlineoffice.db.entity.MessageEntity;
import io.swagger.models.auth.In;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

/**
 * @author yanghan
 * @date 2022/5/6
 */
public interface MessageDao {
    /**
     * 插入消息
     * @param messageEntity 消息实体类
     * @return
     */
    String insert(MessageEntity messageEntity);

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
}
