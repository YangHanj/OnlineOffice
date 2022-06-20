package iee.yh.onlineoffice.common.exception;

/**
 * 通用的异常消息类
 * @author yanghan
 * @date 2022/4/20
 */
public class CommonException extends RuntimeException{
    //异常消息描述
    private String msg;
    //异常码，默认500 (default)
    private int code = 500;

    public CommonException(String msg){
        super(msg);
        this.msg = msg;
    }
    public CommonException(String msg,int code){
        super(msg);
        this.msg = msg;
        this.code = code;
    }
    public CommonException(String msg,Throwable e){
        super(msg,e);
        this.msg = msg;
    }
    public CommonException(String msg,int code,Throwable e){
        super(msg,e);
        this.msg = msg;
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public int getCode() {
        return code;
    }
}
