package iee.yh.onlineoffice.common.config;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import iee.yh.onlineoffice.common.constant.COVID19_City;
import iee.yh.onlineoffice.common.constant.SystemConstants;
import iee.yh.onlineoffice.db.dao.SysConfigDao;
import iee.yh.onlineoffice.db.entity.SysConfigEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.util.List;

/**
 * @author yanghan
 * @date 2022/4/28
 */
@Configuration
@Slf4j
public class GlobalConfig {

    @Autowired
    private SysConfigDao sysConfigDao;
    @Autowired
    private SystemConstants constants;
    @Autowired
    private COVID19_City covid19_city;

    /**
     * 初始化签到时间，项目一启动就从数据库获取
     */
    @PostConstruct
    public void init(){
        LoadChekinTime();
        //LoadCOVID19Area();
    }

    private void LoadChekinTime() {
        List<SysConfigEntity> status = sysConfigDao.selectList(new QueryWrapper<SysConfigEntity>().eq("status", 1));
        status.forEach( info ->{
            String paramKey = info.getParamKey();
            paramKey = StrUtil.toCamelCase(paramKey);
            String paramValue = info.getParamValue();
            try {
                Field declaredField = constants.getClass().getDeclaredField(paramKey);
                declaredField.set(constants,paramValue);
            }catch (Exception e){
                log.error("执行异常",e);
            }
        });
    }

    /**
     *  此方法会将中高风险地区提前加载到内存
     *  并且设置定时任务每天凌晨3：00进行刷新
     */
//    @Scheduled(cron = "00 00 03 * * *")
//    private void LoadCOVID19Area(){
//        HttpRequest request = HttpUtil.createPost("https://diqu.gezhong.vip/api.php");
//        HttpResponse execute = request.execute();
//        String body = execute.body();
//        JSONObject jsonObject = JSONUtil.parseObj(body);
//        JSONObject data = (JSONObject) jsonObject.get("data");
//        covid19_city = JSONUtil.toBean(data, COVID19_City.class);
//        log.info("加载疫情中高风险地区成功!");
//    }
}
