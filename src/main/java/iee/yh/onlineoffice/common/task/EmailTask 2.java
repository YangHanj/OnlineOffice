package iee.yh.onlineoffice.common.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * @author yanghan
 * @date 2022/5/2
 */
@Component
@Scope("prototype")
public class EmailTask implements Serializable {
    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${yang.email.system}")
    private String mailbox;

    @Async
    public void sendAsync(SimpleMailMessage simpleMailMessage){
        simpleMailMessage.setFrom(mailbox);
        javaMailSender.send(simpleMailMessage);
    }
}
