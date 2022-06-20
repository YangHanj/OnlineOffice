package iee.yh.onlineoffice.common.config;

import io.swagger.annotations.ApiOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.ApiSelectorBuilder;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

/**
 * API文档编写
 * @author yanghan
 * @date 2022/4/21
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket getDocket(){

        Docket docket = new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo());
        ApiSelectorBuilder apiSelectorBuilder = docket.select();
        apiSelectorBuilder.apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class));
        docket = apiSelectorBuilder.build();

        //配置token信息，告诉swagger token携带在请求头
        ApiKey apiKey = new ApiKey("token","token","header");
        //存储用户必须提交的参数
        List<ApiKey> list = new ArrayList<>();
        list.add(apiKey);
        docket.securitySchemes(list);

        //如果用户JWT认证通过，则在Swagger中全局有效
        AuthorizationScope authorizationScope = new AuthorizationScope("global","accessEverything");
        AuthorizationScope[] scopes = {authorizationScope};
        //存储令牌和作用域
        SecurityReference securityReference = new SecurityReference("token",scopes);
        List refList = new ArrayList();
        refList.add(securityReference);
        SecurityContext securityContext = SecurityContext.builder().securityReferences(refList).build();
        List ctxList = new ArrayList();
        ctxList.add(securityContext);
        docket.securityContexts(ctxList);

        return docket;
    }

    public ApiInfo apiInfo(){
        Contact contact = new Contact("yang","","yanghan1214@163.com");

        return new ApiInfo( "yang API Doc",
                            "OnlineOfficeMiniApps",
                            "1.0",
                            "urn:tos",
                            contact,
                            "Apache2.0",
                            "",new ArrayList<>() );
    }
}
