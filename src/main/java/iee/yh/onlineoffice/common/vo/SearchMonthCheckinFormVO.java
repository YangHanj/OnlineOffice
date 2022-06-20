package iee.yh.onlineoffice.common.vo;

import io.swagger.annotations.ApiModel;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

/**
 * 查询月签到情况的时候，前端传入的数据
 * @author yanghan
 * @date 2022/5/5
 */
@ApiModel
public class SearchMonthCheckinFormVO {
    @NotNull
    @Range(min=2000,max=3000)
    private Integer year; //年份
    @NotNull
    @Range(min = 1,max = 12)
    private Integer month; //月份

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }
}
