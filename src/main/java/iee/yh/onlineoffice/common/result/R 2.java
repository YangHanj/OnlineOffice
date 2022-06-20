package iee.yh.onlineoffice.common.result;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpStatus;

/**
 * 通用返回值
 * @author yanghan
 * @date 2022/4/20
 */
public class R extends HashMap<String,Object> {
    public R(){
        put("code",HttpStatus.SC_OK);
        put("msg","success");
    }

    public static R error(){
        return error(HttpStatus.SC_INTERNAL_SERVER_ERROR,"未知异常，请联系管理员!");
    }
    public static R error(String msg){
        return error(HttpStatus.SC_INTERNAL_SERVER_ERROR,msg);
    }
    public static R error(int code,String msg){
        R r = new R();
        r.put("code",code);
        r.put("msg",msg);
        return r;
    }
    public static R ok(String msg){
        R r = new R();
        r.put("msg",msg);
        return r;
    }
    public static R ok(Map<String,Object> msg){
        R r = new R();
        r.putAll(msg);
        return r;
    }
    public static R ok(){
        return new R();
    }
    public R put(String key,Object value){
        super.put(key,value);
        return this;
    }
    public Integer getCode(){
        return (Integer) this.get("code");
    }
}