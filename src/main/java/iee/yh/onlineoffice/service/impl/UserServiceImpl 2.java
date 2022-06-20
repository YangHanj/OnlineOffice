package iee.yh.onlineoffice.service.impl;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import iee.yh.onlineoffice.common.exception.CommonException;
import iee.yh.onlineoffice.common.task.MessageTask;
import iee.yh.onlineoffice.db.dao.DeptDao;
import iee.yh.onlineoffice.db.dao.UserDao;
import iee.yh.onlineoffice.db.entity.DeptEntity;
import iee.yh.onlineoffice.db.entity.MessageEntity;
import iee.yh.onlineoffice.db.entity.UserEntity;
import iee.yh.onlineoffice.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author yanghan
 * @date 2022/4/23
 */
@Service
@Slf4j
@Scope("prototype")
public class UserServiceImpl implements UserService {
    @Value("${yang.wx.app-id}")
    private String appId;

    @Value("${yang.wx.app-secret}")
    private String appSecret;

    @Autowired
    private UserDao userDao;

    @Autowired
    private DeptDao deptDao;

    @Autowired
    private MessageTask messageTask;

    /**
     * 获取微信用户在该小程序上的openId
     * 同一个微信在同一个小程序上的openId是永久不变的
     * @param code
     * @return
     */
    private String getOpenId(String code){
        String url = "https://api.weixin.qq.com/sns/jscode2session";
        HashMap<String,Object> map = new HashMap();
        map.put("appid",appId);
        map.put("secret",appSecret);
        map.put("js_code",code);
        map.put("grant_type","authorization_code");
        String response = HttpUtil.post(url, map);
        JSONObject jsonObject = JSONUtil.parseObj(response);
        String openid = jsonObject.getStr("openid");
        if (openid == null || "".equals(openid)){
            throw new RuntimeException("临时登陆凭证错误");
        }
        return openid;
    }

    /**
     * 用户注册
     * @param registerCode 注册邀请码
     * @param code 授权字符串
     * @param nickname 用户的微信名称
     * @param photo 用户的微信头像地址
     * @return 当前用户的主键id
     */
    @Override
    @Transactional
    public Integer registerUser(String registerCode, String code, String nickname, String photo) {
        /*判断邀请码是否为000000，代表为管理员注册*/
        if (registerCode.equals("000000")){
            boolean flag = userDao.haveRootUser();
            if (!flag){
                /**
                 * 当前不存在超级管理员，该用户注册为超级管理员
                 */
                String openId = getOpenId(code);
                UserEntity userEntity = new UserEntity();
                userEntity.setOpenId(openId);
                userEntity.setNickname(nickname);
                userEntity.setPhoto(photo);
                userEntity.setRole("[0]");
                userEntity.setStatus(1);
                userEntity.setCreateTime(new Date());
                userEntity.setRoot(1);
                userDao.insert(userEntity);
                Integer id = userEntity.getId();
                //系统发送注册成功的消息
                //*******************
                MessageEntity messageEntity = new MessageEntity();
                messageEntity.setSenderId(0);
                messageEntity.setSenderName("系统消息");
                messageEntity.setUuid(UUID.randomUUID().toString().replace("-",""));
                messageEntity.setMsg("欢迎您注册成为超级管理员，请及时更新你的员工信息!");
                messageEntity.setSendTime(new Date());
                messageTask.sendAsync(id+"",messageEntity);
                //*******************
                return id;
            }else {
                /**
                 * 已经存在超级管理员
                 */
                throw new CommonException("无法绑定超级管理员账号");
            }
        }else {
            /**
             * 普通员工的注册
             */
        }
        return -1;
    }

    /**
     * 查询用户的权限
     * @param userId 用户id
     * @return
     */
    @Override
    public Set<String> searchUserPermissions(int userId) {
        Set<String> permissions = userDao.searchUserPermissions(userId);
        /*返回权限列表*/
        return permissions;
    }

    /**
     * 用户登陆
     * @param code 临时授权字符串
     * @return
     */
    @Override
    public Integer login(String code){
        /*通过临时授权字符串获取OpenId*/
        String openId = getOpenId(code);
        UserEntity userEntity = userDao.selectOne(
                new QueryWrapper<UserEntity>().eq("open_id", openId)
                                              .and(wapper -> {
                                                   wapper.eq("status", 1);
                                              })
        );
        Integer id = null;
        if (userEntity == null){
            throw new CommonException("账户不存在，请点击下方注册按钮");
        }else {
            id = userEntity.getId();
        }
        //TODO 从消息队列接收消息，转移到消息列表
        //messageTask.receiveAsync(id+"");
        //TODO （已完成）
        return id;
    }

    /**
     * 查询用户
     * @param userId
     * @return
     */
    @Override
    public UserEntity searchById(int userId) {
        UserEntity userEntity = userDao.selectOne(new QueryWrapper<UserEntity>()
                                                        .eq("id", userId)
                                                        .eq("status", 1));
        return userEntity;
    }

    /**
     * 查询员工的入职时间
     * @param userId
     * @return
     */
    @Override
    public String searchUserHiredate(int userId) {
        UserEntity userEntity = userDao.selectOne(new QueryWrapper<UserEntity>()
                                                        .eq("id", userId)
                                                        .eq("status", 1));
        if (userEntity == null)
            return null;
        Date hiredate = userEntity.getHiredate();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = format.format(userEntity.getHiredate());
        return dateStr;
    }

    /**
     * 查询用户信息（用户信息，头像，部门名称）
     * @param userId
     * @return
     */
    @Override
    public HashMap searchUserSummary(int userId) {
        Map<String, Object> map = userDao.selectJoinMap(new MPJLambdaWrapper<UserEntity>()
                                            .select(UserEntity::getName, UserEntity::getPhoto)
                                            .select(DeptEntity::getDeptName)
                                            .leftJoin(DeptEntity.class, DeptEntity::getId, UserEntity::getDeptId)
                                            .eq(UserEntity::getId, userId)
                                            .eq(UserEntity::getStatus, 1)
                                        );
        if (map != null && map.size() > 0)
                return (HashMap) map;
        else return null;
    }

    /**
     * 查询员工信息进行部门分组
     * @param keyword
     * @return
     */
    @Override
    public ArrayList<HashMap> searchUserGroupByDept(String keyword) {
        ArrayList<HashMap> list1 = deptDao.searchDeptMembers(keyword);
        ArrayList<HashMap> list2 = userDao.searchUserGroupByDept(keyword);
        for (HashMap map_1 : list1) {
            Long id = (Long) map_1.get("id");
            ArrayList members = new ArrayList();
            for (HashMap map_2 : list2) {
                Long deptId = (Long)map_2.get("deptId");
                if (deptId.equals(id))
                    members.add(map_2);
            }
            map_1.put("members",members);
        }
        return list1;
    }

    /**
     * 根据员工id集合查询员工信息 id ，name ，photo
     * @param list
     * @return
     */
    @Override
    public List<Map<String, Object>> searchMembers(List list) {
        List<Map<String, Object>> maps = userDao.selectMaps(new QueryWrapper<UserEntity>().in("id", list).select("id", "name", "photo"));
        //TODO......
        return maps;
    }

    @Override
    public List<Map<String, Object>> selectUserPhotoAndName(List param) {
        List<Map<String, Object>> maps = userDao.selectMaps(new QueryWrapper<UserEntity>().eq("status", "1").in("id", param).select("name","photo"));
        return maps;
    }
}
