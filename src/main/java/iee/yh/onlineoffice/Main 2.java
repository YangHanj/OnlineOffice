package iee.yh.onlineoffice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.PostConstruct;

/**
 * @author yanghan
 * @date 2022/4/20
 */
@EnableAsync
@EnableScheduling
@ServletComponentScan
@EnableAspectJAutoProxy
@EnableTransactionManagement
@SpringBootApplication()
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class,args);
    }
}
