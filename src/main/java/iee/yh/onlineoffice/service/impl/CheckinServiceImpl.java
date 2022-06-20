package iee.yh.onlineoffice.service.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateRange;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import iee.yh.onlineoffice.common.constant.SystemConstants;
import iee.yh.onlineoffice.common.exception.CommonException;
import iee.yh.onlineoffice.common.task.EmailTask;
import iee.yh.onlineoffice.common.util.FaceRecongnitionPythonUtils;
import iee.yh.onlineoffice.db.dao.*;
import iee.yh.onlineoffice.db.entity.CheckinEntity;
import iee.yh.onlineoffice.db.entity.CityEntity;
import iee.yh.onlineoffice.db.entity.FaceModelEntity;
import iee.yh.onlineoffice.service.CheckinService;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotBlank;
import java.io.IOException;
import java.text.ParseException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * @author yanghan
 * @date 2022/4/29
 */
@Service
@Scope("prototype")
@Slf4j
public class CheckinServiceImpl implements CheckinService {

    @Autowired
    private SystemConstants systemConstants;

    @Autowired
    private HolidaysDao holidaysDao;

    @Autowired
    private CheckinDao checkinDao;

    @Autowired
    private WorkdayDao workdayDao;

    @Autowired
    private FaceModelDao faceModelDao;

    @Autowired
    private CityDao cityDao;

    @Autowired
    private UserDao userDao;

    @Value("${yang.face.checkinUrl}")
    private String checkinUrl;

    @Value("${yang.face.createFaceModelUrl}")
    private String createFaceModelUrl;

    @Value("${yang.email.hr}")
    private String hrEmail;

    @Value("${yang.code}")
    private String code;

    @Autowired
    private EmailTask emailTask;

    /**
     * 判断能否签到
     * @param userId 用户id
     * @param date 当前签到时间
     * @return
     */
    @Override
    public String validCanCheckin(int userId, @NotBlank String date){
        /**
         * 判断是否为特殊节假日
         */
        boolean isholiday = holidaysDao.searchTodayIsHolidays() != null ? true : false;
        /**
         * 判断是否为特殊工作日
         */
        boolean isworkday = workdayDao.searchTodayIsWorkday() != null ? true : false;
        String type = "工作日";

        if (isWeekend()){
            type = "节假日";
        }
        if (isholiday){
            type = "节假日";
        }else if(isworkday){
            type = "工作日";
        }

        switch (type){
            case "节假日" :
                return "当前为节假日，无需签到";
            case "工作日" :
                /*获取当前签到时间*/
                DateTime now = new DateTime();
                /*获取开始时间与结束时间*/
                String start = DateUtil.today() + " " + systemConstants.attendanceStartTime;
                String end = DateUtil.today() + " " + systemConstants.attendanceEndTime;
                DateTime startTime = DateUtil.parse(start);
                DateTime endTime = DateUtil.parse(end);
                if (now.isBeforeOrEquals(startTime))
                    return "考勤时间未到，请稍后再试";
//                else if (now.isAfterOrEquals(endTime))
//                    return "考勤已结束，您已经迟到了";
                else {
                    HashMap<String,Object> map = new HashMap();
                    map.put("userId",userId);
                    map.put("date",date);
                    map.put("start",start);
                    map.put("end",end);
                    boolean bool = checkinDao.haveCheckin(map) != null ? true : false;
                    return bool ? "已签到，请勿重复签到":"";
                }
            default :
                return null;
        }
    }

    /**
     * 签到
     * @param param
     */
    @Override
    public void checkin(HashMap param) {
        Date d1 = DateUtil.date(); //获取当前时间
        Date d2 = DateUtil.parse(DateUtil.today() + " " + systemConstants.attendanceTime); //上班时间
        Date d3 = DateUtil.parse(DateUtil.today() + " " + systemConstants.attendanceEndTime); //考勤结束时间

        int status = 1;
        if (d1.compareTo(d2) <= 0) status = 1; //正常签到
        else if (d1.compareTo(d2) > 0 && d1.compareTo(d3) <= 0) status = 2; //迟到
        else
            throw new CommonException("考勤时间已过"); //超过签到时间，不再向下执

        Integer userid = (Integer) param.get("userId");
        FaceModelEntity currentUserFaceModel = faceModelDao.selectOne(new QueryWrapper<FaceModelEntity>().eq("user_id", userid));
        if (currentUserFaceModel == null)
            throw new CommonException("不存在人脸模型");
        else {
            //取出用户的人脸图片
            String path = (String) param.get("path");
            //获取该员工的人脸模型
            String faceModel = currentUserFaceModel.getFaceModel();
            //人脸识别
            boolean flag = FaceRecongnitionPythonUtils.runRecongnition(path, faceModel);
//            //向人脸识别发送请求
//            HttpRequest request = HttpUtil.createPost(checkinUrl);
//            request.form("photo", FileUtil.file(path),"targetModel",currentUserFaceModel.getFaceModel());
//            request.form("code",code);
//            HttpResponse response = request.execute();
            if (!flag){
                log.error("人脸识别服务异常");
                throw new CommonException("签到无效,请联系管理员");
            }
           else {
                //TODO 人脸识别正常，判断疫情等级
                int risk = 1;
                String city = (String) param.get("city");
                String district = (String) param.get("district");
                if (city != null && district != null){
                    CityEntity cityEntity = cityDao.selectOne(new QueryWrapper<CityEntity>().eq("city",city));
                    //查询风险地区并发送邮件
                    risk = checkinCOVIDArea(district, cityEntity,userid);
                }
                //TODO 保存记录
                CheckinEntity checkin = new CheckinEntity();
                checkin.setAddress((String) param.get("address"));
                checkin.setCountry((String) param.get("country"));
                checkin.setProvince((String) param.get("province"));
                checkin.setCity(city);
                checkin.setDistrict(district);
                checkin.setStatus(status);
                checkin.setDate(DateUtil.today());
                checkin.setCreateTime(d1);
                checkin.setRisk(risk);
                checkin.setUserId(userid);
                checkinDao.insertData(checkin);
            }
        }
    }



    /**
     *  创建人脸模型
     * @param userId
     * @param path
     */
    @Override
    public void createFaceModel(int userId, String path,String save) {
//        HttpRequest request = HttpUtil.createPost(createFaceModelUrl);
//        request.form("photo",FileUtil.file(path));
//        request.form("code",code);
//        HttpResponse response = request.execute();
//        String body = response.body();
//        if("无法识别人脸".equals(body) || "照片中存在多张人脸".equals(body)){
//            throw new CommonException(body);
//        }else {
//            FaceModelEntity faceModelEntity = new FaceModelEntity();
//            faceModelEntity.setUserId(userId);
//            faceModelEntity.setFaceModel(body);
//            faceModelDao.insert(faceModelEntity);
//        }
        try {
            String modelpath = FaceRecongnitionPythonUtils.runSaveModel(path, save, userId);
            if (StringUtils.isEmpty(modelpath)){
                throw new CommonException("存储人脸模型失败,请重新尝试");
            }
            FaceModelEntity faceModelEntity = new FaceModelEntity();
            faceModelEntity.setFaceModel(modelpath);
            faceModelEntity.setUserId(userId);
            faceModelDao.insert(faceModelEntity);
        } catch (IOException e) {
            e.printStackTrace();
        }catch (Exception e){
            log.error("存储人脸模型失败,请重新尝试");
            throw new CommonException("存储人脸模型失败,请重新尝试");
        }
    }

    /**
     * 查询员工的签到情况
     * @param id 员工id
     * @return
     */
    @Override
    public HashMap searchTodayCheckin(Integer id) {
        HashMap map = checkinDao.searchTodayCheckin(id);
        return map;
    }

    /**
     * 累计签到日期
     * @param id 员工id
     * @return
     */
    @Override
    public long searchCkeckinDays(int id) {
        Integer days = checkinDao.selectCount(new QueryWrapper<CheckinEntity>().eq("user_id", id));
        return days;
    }

    /**
     * 查看某个时间（某一周）段签到情况
     * @param map
     * @return
     */
    @Override
    public ArrayList<HashMap> searchWeekCheckin(HashMap map) {
        /*
         *  本周签到
         *  date status
         */
        ArrayList<HashMap> checkinList = checkinDao.searchWeekCheckin(map);
        /*
         * 本周特殊节假日
         */
        ArrayList<String> SpecialHoilday = holidaysDao.searchHoildayInRange(map);
        /*
         * 本周特殊工作日
         */
        ArrayList<String> SpecialWorkday = workdayDao.searchWorkdayInRange(map);

        DateTime startDate = DateUtil.parseDate(map.get("startDate").toString());
        DateTime endDate = DateUtil.parseDate(map.get("endDate").toString());
        /**
         * 根据参数构建出周对象
         */
        DateRange dateRange = DateUtil.range(startDate, endDate, DateField.DAY_OF_MONTH);

        ArrayList<HashMap> list = new ArrayList<>();
        /**
         * 遍历本周的每一天
         */
        dateRange.forEach( info -> {
            String date = info.toDateStr();
            String type = "工作日";
            //判断是否为周末
            if (info.dayOfWeek() == 1 || info.dayOfWeek() == 7){
                type = "节假日";
            }
            //特殊日期修正
            if (SpecialHoilday != null && SpecialHoilday.size() > 0 && SpecialHoilday.contains(date)){
                type = "节假日";
            }else if (SpecialWorkday != null && SpecialWorkday.size() > 0 && SpecialWorkday.contains(date)){
                type = "工作日";
            }
            String status = "";
            //判断本周以及发生过的日期
            if ("工作日".equals(type) && date.compareTo(DateUtil.date().toDateStr()) <= 0){
                status = "缺勤";
                for (HashMap<String,String> checkin : checkinList){
                    if (checkin.containsValue(date)){
                        status = checkin.get("status");
                        break;
                    }
                }
                DateTime endTime = DateUtil.parse(DateUtil.today()+ " " + systemConstants.attendanceEndTime);
                String curTime = DateUtil.today();
                //恰好为今天，并且目前时间小于签到时间,而且目前未签到
                if (date.equals(curTime) && DateUtil.date().isBeforeOrEquals(endTime) && "缺勤".equals(status)){
                    status = "待签";
                }
            }
            HashMap<String,String> hashMap = new HashMap<>();
            hashMap.put("date",date);
            hashMap.put("status",status);
            hashMap.put("type",type);
            hashMap.put("day",info.dayOfWeekEnum().toChinese("周"));
            list.add(hashMap);
        });
        return list;
    }

    /**
     * 查询月考勤
     * @param map
     * @return
     */
    @Override
    public ArrayList<HashMap> searchMonthCheckin(HashMap map) {
        /**
         * 参数：
         * {"userid":xx,"startTime":"xxx","endTime":"xxx"}
         *
         */
        return this.searchWeekCheckin(map);
    }

    /**
     * 检查中高风险地区
     * @param district 区域
     * @param cityEntity 城市实体类
     * @param userid 用户id
     * @return
     */
    private int checkinCOVIDArea(String district, CityEntity cityEntity,int userid) {
        int risk;
        try{
            String url = "http://m." + cityEntity.getCode() + ".bendibao.com/news/yqdengji/?qu=" + district;
            Document document = Jsoup.connect(url).get();
            //CSS样式查询
            Elements elementsByClass = document.getElementsByClass("list-content");
            if(elementsByClass.size() > 0){
                Element element = elementsByClass.get(0);
                //CSS样式查询
                String text = element.select("p:last-child").text();
                if ("高风险".equals(text) || "中风险".equals(text)){
                    risk = 2;
                    //TODO 高风地区险发送邮件
                    sendMail(cityEntity.getCity(), district, userid);
                    return risk;
                }else if ("低风险".equals(text)){
                    risk = 1;
                    return risk;
                }else throw new CommonException("获取风险等级失败");
            }
        }catch (Exception e){
            log.error("获取风险等级失败");
            throw new CommonException("获取风险等级失败");
        }
        return 0;
    }

    /**
     * 发送邮件
     * @param city 城市
     * @param district 区域
     * @param userid 用户id
     */
    private void sendMail(String city, String district, int userid) {
        //采用异步多线程发送
        HashMap<String, String> map = userDao.searchNameAndDept(userid);
        String name = map.get("name");
        String deptName = map.get("dept_name");
        deptName = deptName != null ? deptName : "";
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(hrEmail);
        mailMessage.setSubject("员工"+name+"签到地点为中高风险地区");
        mailMessage.setText(
                deptName + "员工" + name + "," + DateUtil.format(new Date(),"yyyy年MM月dd日")
                        + "处于:" + city + " " + district + ",属于新冠疫情中高风险地区，请及时与员工联系核实!" );
        emailTask.sendAsync(mailMessage);
    }

    /**
     * 判断当前日期是否为周六或者周末
     * @return
     * @throws ParseException
     */
    private boolean isWeekend() {
        LocalDateTime localDateTime = LocalDateTime.now();
        DayOfWeek dayOfWeek = localDateTime.getDayOfWeek();
        if(dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY){
            return true;
        } else{
            return false;
        }
    }
}
