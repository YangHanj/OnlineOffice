package iee.yh.onlineoffice.db.dao;

import iee.yh.onlineoffice.db.entity.RoleEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色表
 * 
 * @author yanghan
 * @email yanghan1214@163.com
 * @date 2022-04-20 16:15:12
 */
@Mapper
public interface RoleDao extends BaseMapper<RoleEntity> {
	
}
