package iee.yh.onlineoffice.db.dao;

import iee.yh.onlineoffice.db.entity.WorkdayEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * 
 * @author yanghan
 * @email yanghan1214@163.com
 * @date 2022-04-20 16:15:12
 */
@Mapper
public interface WorkdayDao extends BaseMapper<WorkdayEntity> {
    /**
     * 判断当前是否为工作日
     * @return
     */
	Integer searchTodayIsWorkday();

    /**
     * 查询某个时间段的特殊工作日
     * @param map
     * @return
     */
    ArrayList<String> searchWorkdayInRange(HashMap map);
}
