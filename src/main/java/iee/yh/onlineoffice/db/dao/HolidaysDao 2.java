package iee.yh.onlineoffice.db.dao;

import iee.yh.onlineoffice.db.entity.HolidaysEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 节假日表
 * 
 * @author yanghan
 * @email yanghan1214@163.com
 * @date 2022-04-20 16:15:12
 */
@Mapper
public interface HolidaysDao extends BaseMapper<HolidaysEntity> {
    /**
     * 判断当前日期是否为特殊节假日
     * @return
     */
    Integer searchTodayIsHolidays();

    /**
     * 查询某个时间段内的节假日
     * @param map
     * @return
     */
    ArrayList<String> searchHoildayInRange(HashMap map);
}
