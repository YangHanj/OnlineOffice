package demo;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdcardUtil;
import iee.yh.onlineoffice.Main;
import iee.yh.onlineoffice.db.dao.MeetingDao;
import iee.yh.onlineoffice.db.entity.MeetingEntity;
import iee.yh.onlineoffice.db.entity.MessageEntity;
import iee.yh.onlineoffice.db.entity.MessageRefEntity;
import iee.yh.onlineoffice.service.MeetingService;
import iee.yh.onlineoffice.service.MessageService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.junit4.SpringRunner;
import java.util.Date;
import java.util.UUID;


/**
 * @author yanghan
 * @date 2022/5/1
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Main.class})
public class demo {
    @Autowired
    private MessageService messageService;
    @Autowired
    private MeetingService meetingService;

    // 创建消息测试类
    //@Test
    public void contextLoads(){
        for (int i = 0; i < 100; i++) {
            MessageEntity messageEntity = new MessageEntity();
            messageEntity.setUuid(UUID.randomUUID().toString().replace("-",""));
            messageEntity.setSenderId(0);
            messageEntity.setSenderName("系统消息");
            messageEntity.setMsg("这是第"+i+"条消息");
            messageEntity.setSendTime(new Date());
            String id = messageService.insertMessage(messageEntity);

            MessageRefEntity refEntity = new MessageRefEntity();
            refEntity.setMessageId(id);
            refEntity.setReceiverId(3);
            refEntity.setReadFlag(false);
            refEntity.setLastFlag(true);
            messageService.insertRef(refEntity);
        }
    }


    //创建会议测试类
    //@Test
    public void creatMeeting(){
        for (int i = 1; i <= 50; i++) {
            MeetingEntity meetingEntity = new MeetingEntity();
            //meetingEntity.setId((long)i);
            meetingEntity.setUuid(UUID.randomUUID().toString().replace("-",""));
            meetingEntity.setTitle("测试会议"+i);
            meetingEntity.setCreatorId(3l);//ROOT用户id
            meetingEntity.setDate(DateUtil.today());
            //meetingEntity.setPlace("");
            meetingEntity.setStart("08:30");
            meetingEntity.setEnd("10:30");
            meetingEntity.setType((short)1);
            meetingEntity.setMembers("[3,4]");
            meetingEntity.setDesc("线上会议测试");
            meetingEntity.setInstanceId(UUID.randomUUID().toString().replace("-",""));
            meetingEntity.setStatus((short)1);
            meetingEntity.setCreateTime(new Date());
            meetingService.insertMeeting(meetingEntity);
        }
    }

}
