package iee.yh.onlineoffice.db.dao;

import com.github.yulichang.base.MPJBaseMapper;
import iee.yh.onlineoffice.Main;
import iee.yh.onlineoffice.db.entity.UserEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * 用户表
 * 
 * @author yanghan
 * @email yanghan1214@163.com
 * @date 2022-04-20 16:15:12
 */
@Mapper
public interface UserDao extends MPJBaseMapper<UserEntity> {
    /**
     * 判断是否存在root用户
     * @return
     */
	boolean haveRootUser();

    /**
     * 按照openId查询在职状态的员工id
     * @param openId
     * @return
     */
    Integer searchIdByOpenId(@Param("openId") String openId);

    /**
     * 查询用户的权限
     * 用户表 <----> 权限表 <----> 角色表
     * 通过用户表查找出自己对应的角色u.role -> r.id
     * 通过对应的角色查找出对应的权限
     * @param userId 用户id
     * @return
     */
    Set<String> searchUserPermissions(@Param("userId") int userId);

    /**
     * 通过用户id查询用户与部门
     * @param uderid
     * @return
     */
    HashMap<String,String> searchNameAndDept(@Param("userId")int uderid);

    /**
     * 查询员工信息进行部门分组
     * @param keyword
     * @return
     */
    ArrayList<HashMap> searchUserGroupByDept(String keyword);

    HashMap searchUserInfo(int userId);

    int searchDeptManagerId(int id);

    int searchGmId();

    /**
     * 根据用户id查询用户的部门名称
     * @param userId
     * @return
     */
    HashMap searchDeptNameByUserId(int userId);
}
