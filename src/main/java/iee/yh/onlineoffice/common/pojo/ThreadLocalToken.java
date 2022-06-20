package iee.yh.onlineoffice.common.pojo;

import org.springframework.stereotype.Component;

/**
 * 媒介类
 * 将令牌保存在ThreadLocal
 * @author yanghan
 * @date 2022/4/22
 */
@Component
public class ThreadLocalToken {
    private ThreadLocal<String> local = new ThreadLocal();

    public void setToken(String token){
        local.set(token);
    }

    public String getToken(){
        return local.get();
    }

    public void clear(){
        local.remove();
    }
}
