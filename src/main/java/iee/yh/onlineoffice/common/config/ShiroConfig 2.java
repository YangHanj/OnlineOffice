package iee.yh.onlineoffice.common.config;

import iee.yh.onlineoffice.common.config.filter.OAuth2Filter;
import iee.yh.onlineoffice.common.util.OAuth2Realm;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.mgt.SecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author yanghan
 * @date 2022/4/21
 */
@Configuration
public class ShiroConfig {
    /**
     * 配置安全管理器
     * @param oauth2Realm
     * @return
     */
    @Bean("securityManager")
    public SecurityManager getsecurityManager(@Autowired OAuth2Realm oauth2Realm){
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(oauth2Realm);
        securityManager.setRememberMeManager(null);
        return securityManager;
    }

    /**
     * 设置 Shiro 的url拦截规则、拦截器
     * @param securityManager
     * @return
     */
    @Bean("shiroFilter")
    public ShiroFilterFactoryBean getshiroFilter(SecurityManager securityManager, @Autowired OAuth2Filter filter){
        ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
        shiroFilter.setSecurityManager(securityManager);

        /*设置自定义的拦截器Filter*/
        Map<String, Filter> filters = new HashMap<>();
        //urlFilterMap.put("/**", filter);
        filters.put("oauth2", filter);
        shiroFilter.setFilters(filters);

        // 设置 url 拦截规则, 拦截时将按定义顺序进行匹配，故 /** 规则应放在最后
        Map<String, String> urlFilterMap = new LinkedHashMap<>();
        // anon 表示该url可匿名访问
        urlFilterMap.put("/webjars/**", "anon");
        urlFilterMap.put("/druid/**", "anon");
        urlFilterMap.put("/app/**", "anon");
        urlFilterMap.put("/sys/login", "anon");
        urlFilterMap.put("/swagger/**", "anon");
        urlFilterMap.put("/v2/api-docs", "anon");
        urlFilterMap.put("/swagger-ui.html", "anon");
        urlFilterMap.put("/swagger-resources/**", "anon");
        urlFilterMap.put("/captcha.jpg", "anon");
        urlFilterMap.put("/user/register", "anon");
        urlFilterMap.put("/user/login", "anon");
        urlFilterMap.put("/test/**", "anon");
        /*放行签到，以便测试*/
//        urlFilterMap.put("/checkin/validCanCheckIn","anon");
        // customFilter 表示该url将由指定的拦截器 customFilter 进行拦截处理
        urlFilterMap.put("/**", "oauth2");
        shiroFilter.setFilterChainDefinitionMap(urlFilterMap);

        return shiroFilter;
    }

    @Bean
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor(){
        return new LifecycleBeanPostProcessor();
    }

    @Bean
    @DependsOn("lifecycleBeanPostProcessor")
    public DefaultAdvisorAutoProxyCreator getDefaultAdvisorAutoProxyCreator(){
        DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        defaultAdvisorAutoProxyCreator.setProxyTargetClass(true);
        return defaultAdvisorAutoProxyCreator;
    }

    //配置Shiro通知器
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }

}
