package iee.yh.onlineoffice.db.dao;

import iee.yh.onlineoffice.db.entity.DeptEntity;
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
public interface DeptDao extends BaseMapper<DeptEntity> {
    /**
     * 根据姓名模糊查询员工信息和部门信息
     * @param keyword
     * @return
     */
    ArrayList<HashMap> searchDeptMembers(String keyword);
}
