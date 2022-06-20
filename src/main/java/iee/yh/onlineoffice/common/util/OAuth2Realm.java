package iee.yh.onlineoffice.common.util;

import iee.yh.onlineoffice.common.pojo.OAuth2Token;
import iee.yh.onlineoffice.db.entity.UserEntity;
import iee.yh.onlineoffice.service.UserService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 封装权限检查方法
 * @author yanghan
 * @date 2022/4/22
 */
@Component
public class OAuth2Realm extends AuthorizingRealm {

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof OAuth2Token;
    }

    @Autowired
    private UserService userService;

    /**
     * 授权（验证权限限时调用）
     * @param principalCollection
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        //TODO 查询用户权限列表
        UserEntity userEntity = (UserEntity) principalCollection.getPrimaryPrincipal();
        Integer id = userEntity.getId();
        Set<String> permissions = userService.searchUserPermissions(id);
        //TODO 把权限列表添加到info对象中
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.setStringPermissions(permissions);
        return info;
    }
    /**
     * 认证（验证登陆时调用）
     * @param authenticationToken
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        //TODO 从令牌获取userid，然后检查账户
        String accessToken = (String) authenticationToken.getPrincipal();
        int userId = jwtUtils.getUserId(accessToken);
        UserEntity userEntity = userService.searchById(userId);
        if (userEntity == null)
            throw new LockedAccountException("账户已经被锁定，请联系管理员");
        //TODO 往info对象中添加用户信息，Token字符串
        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(userEntity,accessToken,getName());
        return info;
    }
}
