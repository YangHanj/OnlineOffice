package iee.yh.onlineoffice.controller;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import iee.yh.onlineoffice.common.constant.SystemConstants;
import iee.yh.onlineoffice.common.exception.CommonException;
import iee.yh.onlineoffice.common.result.R;
import iee.yh.onlineoffice.common.util.JwtUtils;
import iee.yh.onlineoffice.common.vo.CheckinFormVO;
import iee.yh.onlineoffice.common.vo.SearchMonthCheckinFormVO;
import iee.yh.onlineoffice.service.CheckinService;
import iee.yh.onlineoffice.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author yanghan
 * @date 2022/4/30
 */
@Slf4j
@Api("签到模块")
@RestController
@RequestMapping("/checkin")
public class CheckinController {
    @Autowired
    private CheckinService checkinService;

    @Autowired
    private UserService userService;

    @Autowired
    private SystemConstants systemConstants;

    @Autowired
    private JwtUtils jwtUtils;

    @Value("${yang.image-folder}")
    private String imageFoldar;

    @Value("${yang.face-model-folder}")
    private String save;

    @GetMapping("/validCanCheckIn")
    @ApiOperation("查看用户当前是否可以签到")
    public R validCanCheckin(@RequestHeader("token") String token) throws ParseException {
        int userId = jwtUtils.getUserId(token);
        String res = checkinService.validCanCheckin(userId, DateUtil.today());
        return R.ok(res);
    }

    @PostMapping("/checkin")
    @ApiOperation("签到")
    public R checkin(@Valid CheckinFormVO fromVo,
                     @RequestParam("photo") MultipartFile file,
                     @RequestHeader("token") String token){

        if (null == file){
            return R.error("没有上传文件");
        }
        int userId = jwtUtils.getUserId(token);
        String filename = file.getOriginalFilename().toLowerCase();
        if (!filename.endsWith(".jpg") && !filename.endsWith(".png")){
            return R.error("必须提交jpg或者png格式图片");
        }else {
            //CheckinFormVO fromVo = new CheckinFormVO();
            String path = imageFoldar + "/" + filename;
            try {
                //图片保存
                file.transferTo(Paths.get(path));
                /*****防止图片上传延迟*****/
                TimeUnit.SECONDS.sleep(1);
                /***********************/
                HashMap param = new HashMap();
                param.put("userId",userId);
                param.put("path",path);
                param.put("city",fromVo.getCity());
                param.put("district",fromVo.getDistrict());
                param.put("address",fromVo.getAddress());
                param.put("country",fromVo.getCountry());
                param.put("province",fromVo.getProvince());
                checkinService.checkin(param);
                return R.ok("签到成功");
            } catch (CommonException e) {
                log.error(e.getMessage());
                throw e;
            }catch (Exception e){
                log.error(e.getMessage());
                throw new CommonException("系统异常，请稍后重试");
            }
            finally {
                //删除图像
                FileUtil.del(path);
            }
        }
    }

    /**
     * （测试通过）
     * eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE2NTE5ODg4MTEsInVzZXJJZCI6M30.VvhjKR6eO8pTkZBYGA5Uh1P6NBf41-w8BlR_ai5hsEE
     * @param file 文件路径
     * @param token
     * @return
     */
    @PostMapping("/createFaceModel")
    @ApiOperation("创建人脸模型")
    public R createFaceModel(@RequestParam("photo")MultipartFile file,
                             @RequestHeader("token") String token){
        int userId = jwtUtils.getUserId(token);
        if (file == null)
            return R.error("没有上传文件");
        String filename = file.getOriginalFilename().toLowerCase();
        String path = imageFoldar + "/" + filename;
        if (!filename.endsWith(".jpg") && !filename.endsWith(".png")){
            return R.error("必须提交jpg或者png格式图片");
        }else {
            try{
                file.transferTo(Paths.get(path));
                /*****防止图片上传延迟*****/
                TimeUnit.SECONDS.sleep(1);
                /***********************/
                checkinService.createFaceModel(userId,path,save);
                return R.ok("人脸建模成功");
            }catch (Exception e){
                log.error(e.getMessage());
                throw new CommonException("保存图片错误");
            }finally {
                FileUtil.del(path);
            }
        }
    }

    /**
     * 读取用户当天的签到情况（测试通过）
     * @param token
     * @return
     */
    @GetMapping("/searchTodayCheckin")
    @ApiOperation("查询用户当日签到数据")
    public HashMap searchTodayCheckin(@RequestHeader("token") String token){
        int userId = jwtUtils.getUserId(token);
        HashMap map = checkinService.searchTodayCheckin(userId);
        map.put("attendanceTime",systemConstants.attendanceTime);
        map.put("closingTime",systemConstants.closingTime);
        long days = checkinService.searchCkeckinDays(userId);
        map.put("checkinDays",days);
        //判断入职日期
        DateTime hiredate = DateUtil.parse(userService.searchUserHiredate(userId));
        //当前日期所在周的周一（当前周的开始时间）
        DateTime beginOfWeek = DateUtil.beginOfWeek(DateUtil.date());
        if (beginOfWeek.isBeforeOrEquals(hiredate)){
            beginOfWeek = hiredate;
        }
        DateTime endOfWeek = DateUtil.endOfWeek(DateUtil.date());
        HashMap param = new HashMap();
        param.put("startDate",beginOfWeek.toDateStr());
        param.put("endDate",endOfWeek.toDateStr());
        param.put("userId",userId);
        ArrayList<HashMap> checkin = checkinService.searchWeekCheckin(param);
        map.put("weekCheckin",checkin);
        return R.ok().put("result",map);
    }

    @PostMapping("searchMonthCheckin")
    @ApiOperation("查询用户月签到情况")
    public R searchMonthCheckin(@RequestHeader("token") String token,
                                @RequestBody SearchMonthCheckinFormVO searchMonthCheckinFormVO){
        int userId = jwtUtils.getUserId(token);
        //查询入职日期
        DateTime hiredate = DateUtil.parse(userService.searchUserHiredate(userId));
        //参数处理
        String month = searchMonthCheckinFormVO.getMonth() < 10 ? "0"+searchMonthCheckinFormVO.getMonth():searchMonthCheckinFormVO.getMonth().toString();
        //起始日期
        DateTime startdate = DateUtil.parse(searchMonthCheckinFormVO.getYear() + "-" + month + "-01");
        //查询月 < 入职月份
        if (startdate.isBeforeOrEquals(DateUtil.beginOfMonth(hiredate))){
            log.error("只能查询入职之后的日期数据");
            throw new CommonException("只能查询入职之后的日期数据");
        }
        if (startdate.isBeforeOrEquals(hiredate)){
            startdate = hiredate;
        }
        DateTime enddate = DateUtil.endOfMonth(startdate);
        HashMap map = new HashMap<>();
        map.put("userId",userId);
        map.put("startDate",startdate.toDateStr());
        map.put("endDate",enddate.toDateStr());
        ArrayList<HashMap> list = checkinService.searchMonthCheckin(map);

        int sum1=0,sum2=0,sum3=0;
        for (HashMap<String,String> info : list){
            //当前是工作日还是节假日
            String type = info.get("type");
            String status = info.get("status");
            if ("工作日".equals(type)){
                if ("正常".equals(status))
                    sum1++;
                else if ("迟到".equals(status))
                    sum2++;
                else if ("缺勤".equals(status))
                    sum3++;
            }
        }
        return R.ok().put("list",list)
                     .put("sum_1",sum1)
                     .put("sum_2",sum2)
                     .put("sum_3",sum3);
    }
}
