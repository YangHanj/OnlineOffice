package iee.yh.onlineoffice.common.config;

import com.rabbitmq.client.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author yanghan
 * @date 2022/5/8
 */
@Configuration
public class RabbitMQConfig {
    /**
     * 同步收发消息需要采用ConnectionFactory
     * 所以需要在配置类声明
     * 如果是异步，则只需要在配置文件声明
     * @return
     */
    @Bean
    public ConnectionFactory getFactory(){
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("127.0.0.1");
        connectionFactory.setPort(5672);
        return connectionFactory;
    }
}
