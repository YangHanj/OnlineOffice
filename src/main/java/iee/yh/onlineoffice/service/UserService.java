package iee.yh.onlineoffice.service;

import iee.yh.onlineoffice.db.entity.UserEntity;

import java.util.*;

/**
 * @author yanghan
 * @date 2022/4/23
 */
public interface UserService {
    /**
     * 用户注册
     * @param registerCode 注册邀请码
     * @param code 授权字符串
     * @param nickname 用户的微信名称
     * @param photo 用户的微信头像地址
     * @return
     */
    Integer registerUser(String registerCode,String code,String nickname,String photo);

    /**
     * 查找用户权限
     * @param userId 用户的id
     * @return
     */
    Set<String> searchUserPermissions(int userId);

    /**
     * 用户登陆
     * @param code 临时授权字符串
     * @return
     */
    Integer login(String code);

    /**
     * 查询用户
     * @param userId
     * @return
     */
    UserEntity searchById(int userId);

    /**
     * 查询员工的入职时间
     * @param userId
     * @return
     */
    String searchUserHiredate(int userId);

    /**
     * 查询用户信息（用户信息，头像，部门名称）
     * @param userId
     * @return
     */
    HashMap searchUserSummary(int userId);

    /**
     * 查询员工信息进行部门分组
     * @param keyword
     * @return
     */
    ArrayList<HashMap> searchUserGroupByDept(String keyword);

    /**
     * 根据员工id集合查询员工信息 id ，name ，photo
     * @param list
     * @return
     */
    List<Map<String, Object>> searchMembers(List list);

    /**
     * 查询用户的照片与名称
     * @param param
     * @return
     */
    List<Map<String, Object>> selectUserPhotoAndName(List param);
}
