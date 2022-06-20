package iee.yh.onlineoffice.common.util;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import iee.yh.onlineoffice.common.constant.ExceptionEnum;
import iee.yh.onlineoffice.common.exception.CommonException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;


/**
 * 一个简单的JWT工具类
 * 实现了简单的加密，解密，以及验证功能
 * 其中密钥自己在配置文件中添加
 * 后期可以通过KeyPairGeneration生成真正的密钥使用
 * @author yanghan
 * @date 2022/4/21
 */
@Component
@Slf4j
public class JwtUtils {
    @Value("${yang.jwt.secret}")
    private String secret;
    @Value("${yang.jwt.expire}")
    private Integer expire;

    //加密
    public String createToken(int userId){
        //计算过期时间
        Date date = DateUtil.offset(new Date(), DateField.DAY_OF_YEAR,expire);
        Algorithm algorithm = Algorithm.HMAC256(secret);
        JWTCreator.Builder builder = JWT.create();
        String token = builder.withClaim("userId", userId).withExpiresAt(date).sign(algorithm);
        return token;
    }
    //解密
    public int getUserId(String token) {
        try{
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim("userId").asInt();
        }catch (Exception e){
            throw new CommonException(ExceptionEnum.JWTERROR.getMsg(),ExceptionEnum.JWTERROR.getCode());
        }
    }

    //验证
    public void verifierToken(String token){
        Algorithm algorithm = Algorithm.HMAC256(secret);
        JWTVerifier verifier = JWT.require(algorithm).build();
        verifier.verify(token);
    }
}
