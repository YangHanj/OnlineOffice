package iee.yh.onlineoffice.common.aop;

import iee.yh.onlineoffice.common.pojo.ThreadLocalToken;
import iee.yh.onlineoffice.common.result.R;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author yanghan
 * @date 2022/4/22
 */
@Component
@Aspect
public class TokenAspect {
    @Autowired
    private ThreadLocalToken threadLocalToken;

    @Pointcut("execution(public * iee.yh.onlineoffice.controller.*.*(..))")
    public void aspect(){}

    @Around("aspect()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        /*方法执行结果*/
        R r = (R)joinPoint.proceed();
        String token = threadLocalToken.getToken();
        /*如果ThreadLocal中存在token，说明是更新的token*/
        if (token != null){
            /*往响应中放置token*/
            r.put("token",token);
            threadLocalToken.clear();
        }
        return r;
    }
}
