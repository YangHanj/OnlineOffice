package iee.yh.onlineoffice.common.constant;

import org.springframework.stereotype.Component;

/**
 * 签到时间信息常量
 * @author yanghan
 * @date 2022/4/28
 */
@Component
public class SystemConstants {
    /**
     * 上班考勤开始时间
     */
    public String attendanceStartTime;
    /**
     * 上班时间
     */
    public String attendanceTime;
    /**
     * 上班考勤截止时间
     */
    public String attendanceEndTime;
    /**
     * 下班考勤开始时间
     */
    public String closingStartTime;
    /**
     * 下班时间
     */
    public String closingTime;
    /**
     * 下班考勤截止时间
     */
    public String closingEndTime;

    public String getAttendanceStartTime() {
        return attendanceStartTime;
    }

    public void setAttendanceStartTime(String attendanceStartTime) {
        this.attendanceStartTime = attendanceStartTime;
    }

    public String getAttendanceTime() {
        return attendanceTime;
    }

    public void setAttendanceTime(String attendanceTime) {
        this.attendanceTime = attendanceTime;
    }

    public String getAttendanceEndTime() {
        return attendanceEndTime;
    }

    public void setAttendanceEndTime(String attendanceEndTime) {
        this.attendanceEndTime = attendanceEndTime;
    }

    public String getClosingStartTime() {
        return closingStartTime;
    }

    public void setClosingStartTime(String closingStartTime) {
        this.closingStartTime = closingStartTime;
    }

    public String getClosingTime() {
        return closingTime;
    }

    public void setClosingTime(String closingTime) {
        this.closingTime = closingTime;
    }

    public String getClosingEndTime() {
        return closingEndTime;
    }

    public void setClosingEndTime(String closingEndTime) {
        this.closingEndTime = closingEndTime;
    }
}
