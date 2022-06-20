package iee.yh.onlineoffice.common.config.filter;

import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import iee.yh.onlineoffice.common.pojo.OAuth2Token;
import iee.yh.onlineoffice.common.pojo.ThreadLocalToken;
import iee.yh.onlineoffice.common.util.JwtUtils;
import org.apache.http.HttpStatus;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * 有关shiro的过滤器
 * @author yanghan
 * @date 2022/4/21
 */
@Component(value = "oAuth2Filter")
@Scope("prototype")
public class OAuth2Filter extends AuthenticatingFilter {
    @Autowired
    private ThreadLocalToken threadLocalToken;

    @Value("${yang.jwt.cache-expire}")
    private String cacheExpire;

    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 拦截请求，用于把令牌字符串封装为令牌对象
     * @param servletRequest
     * @param servletResponse
     * @return
     * @throws Exception
     */
    @Override
    protected AuthenticationToken createToken(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        /*获取token信息*/
        String token = getRequestToken((HttpServletRequest)servletRequest);
        if (StringUtils.isBlank(token))
            return null;
        return new OAuth2Token(token);
    }

    /**
     * 判断请求是否需要被处理
     * ajax提交的application/json数据，会首先发出Option请求
     * 如果是option请求，则不做处理，直接放行
     * @param request
     * @param response
     * @param mappedValue
     * @return
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        if (httpServletRequest.getMethod().equals(RequestMethod.OPTIONS.name()))
            return true;
        return false;
    }

    /**
     * 过滤器处理方法
     * @param servletRequest
     * @param servletResponse
     * @return
     * @throws Exception
     */
    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        AllowCROS(request,response);

        /*清空ThreadLocal数据*/
        threadLocalToken.clear();

        String token = getRequestToken(request);
        if (StrUtil.isBlank(token)) {
            response.setStatus(HttpStatus.SC_UNAUTHORIZED);
            response.getWriter().println("无效的令牌");
            return false;
        }
        /*验证*/
        try {
            jwtUtils.verifierToken(token);
        } catch (TokenExpiredException e) {
            /*查看缓存中是否有数据*/
            /*客户端令牌过期，缓存中未过期*/
            if (redisTemplate.hasKey(token)){
                redisTemplate.delete(token);
                int userId = jwtUtils.getUserId(token);
                /*生成新的令牌*/
                token = jwtUtils.createToken(userId);
                /*新令牌存入缓存*/
                redisTemplate.opsForValue().set(token,userId+"", Long.parseLong(cacheExpire), TimeUnit.DAYS);
                threadLocalToken.setToken(token);
            }else {
                /*客户端与缓存令牌均过期*/
                /*用户重新登陆*/
                response.setStatus(HttpStatus.SC_UNAUTHORIZED);
                response.getWriter().println("登陆失效，请重新登陆");
                return false;
            }
        }catch (Exception e) {
            response.setStatus(HttpStatus.SC_UNAUTHORIZED);
            response.getWriter().println("无效的令牌");
            return false;
        }
        boolean login = executeLogin(servletRequest, servletResponse);
        /*true:认证成功 false:认证失败*/
        return login;
    }

    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request, ServletResponse response) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        AllowCROS(httpServletRequest,httpServletResponse);
        httpServletResponse.setStatus(HttpStatus.SC_UNAUTHORIZED);
        try {
            httpServletResponse.getWriter().println(e.getMessage());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * 获取token
     * @param servletRequest
     * @return
     */
    private String getRequestToken(HttpServletRequest servletRequest) {
        String header = servletRequest.getHeader("token");
        if (StrUtil.isBlank(header))
            return servletRequest.getParameter("token");
        return header;
    }

    /**
     * 跨域问题解决
     * @param servletRequest
     * @param servletResponse
     */
    private void AllowCROS(HttpServletRequest servletRequest,HttpServletResponse servletResponse) {
        servletResponse.setContentType("text/html");
        servletResponse.setCharacterEncoding("UTF-8");
        /*允许跨域*/
        servletResponse.setHeader("Access-Control-Allow-Credentials","true");
        servletResponse.setHeader("Access-Control-Allow-Origin",servletRequest.getHeader("Origin"));
    }
}
