package iee.yh.onlineoffice.common.pojo;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * 该类用来保存token字符串
 * @author yanghan
 * @date 2022/4/22
 */
public class OAuth2Token implements AuthenticationToken {

    private String token;

    public OAuth2Token(String token) {
        this.token = token;
    }

    @Override
    public String getPrincipal() {
        return token;
    }

    @Override
    public String getCredentials() {
        return token;
    }
}
