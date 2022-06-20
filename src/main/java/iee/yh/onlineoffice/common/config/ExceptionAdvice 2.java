package iee.yh.onlineoffice.common.config;

import iee.yh.onlineoffice.common.exception.CommonException;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.UnauthenticatedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

/**
 * 全局异常处理
 * @author yanghan
 * @date 2022/4/22
 */
@RestControllerAdvice
@Slf4j
public class ExceptionAdvice {
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({Exception.class})
    public String validExceptionHandler(Exception e){
        log.error("执行异常",e);
        if (e instanceof MethodArgumentNotValidException){
            MethodArgumentNotValidException exception = (MethodArgumentNotValidException) e;
            /*将错误信息返回给前台*/
            return exception.getBindingResult().getFieldError().getDefaultMessage();
        }else if(e instanceof CommonException){
            CommonException exception = (CommonException) e;
            return exception.getMsg();
        }else if (e instanceof UnauthenticatedException){
            return "您不具有相关权限!";
        }else return "后端执行异常!";
    }
}
