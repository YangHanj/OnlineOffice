package iee.yh.onlineoffice.db.dao;

import iee.yh.onlineoffice.db.entity.CheckinEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 签到表
 * 
 * @author yanghan
 * @email yanghan1214@163.com
 * @date 2022-04-20 16:15:12
 */
@Mapper
public interface CheckinDao extends BaseMapper<CheckinEntity> {
    /**
     * 判断是否已经签过到了
     * @param map
     * @return
     */
    Integer haveCheckin(HashMap<String,Object> map);

    /**
     * 插入数据
     * @param checkinEntity
     */
    void insertData(CheckinEntity checkinEntity);

    /**
     * 查询员工的签到情况
     * @param id 员工id
     * @return
     */
    HashMap searchTodayCheckin(Integer id);

    /**
     * 查看某个时间（某一周）段签到情况
     * @param map
     * @return
     */
    ArrayList<HashMap> searchWeekCheckin(HashMap map);
}
