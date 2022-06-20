package iee.yh.onlineoffice.common.constant;

/**
 * 定义一个通用异常枚举
 * @author yanghan
 * @date 2022/4/20
 */
public enum ExceptionEnum{
    //TODO 异常枚举类
    ERROR("",500),
    JWTERROR("权限认证错误!",555);

    private String msg;
    private int code;
    ExceptionEnum(String msg,int code){
        this.code = code;
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public int getCode() {
        return code;
    }
}
