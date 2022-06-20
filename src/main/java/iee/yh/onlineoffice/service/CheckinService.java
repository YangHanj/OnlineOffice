package iee.yh.onlineoffice.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author yanghan
 * @date 2022/4/29
 */
public interface CheckinService {
    /**
     * 判断能否签到
     * @param userId 用户id
     * @param date 当前签到时间
     * @return
     */
    String validCanCheckin(int userId,String date) throws ParseException;

    /**
     * 签到
     * @param param
     */
    void checkin(HashMap param);

    /**
     * 创建人脸模型
     * @param userId
     * @param path
     */
    void createFaceModel(int userId,String path,String save);

    /**
     * 查询员工的签到情况
     * @param id 员工id
     * @return
     */
    HashMap searchTodayCheckin(Integer id);

    /**
     * 累计签到日期
     * @param id 员工id
     * @return
     */
    long searchCkeckinDays(int id);

    /**
     * 查看某个时间（某一周）段签到情况
     * @param map
     * @return
     */
    ArrayList<HashMap> searchWeekCheckin(HashMap map);

    /**
     * 查询月考勤
     * @param map
     * @return
     */
    ArrayList<HashMap> searchMonthCheckin(HashMap map);

}
