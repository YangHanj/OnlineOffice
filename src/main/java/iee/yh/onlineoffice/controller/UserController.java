package iee.yh.onlineoffice.controller;

import cn.hutool.json.JSONUtil;
import iee.yh.onlineoffice.common.exception.CommonException;
import iee.yh.onlineoffice.common.result.R;
import iee.yh.onlineoffice.common.util.JwtUtils;
import iee.yh.onlineoffice.common.vo.*;
import iee.yh.onlineoffice.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

/**
 * 用户控制层
 * @author yanghan
 * @date 2022/4/24
 */
@RestController
@RequestMapping("/user")
@Api("用户模块Web接口")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${yang.jwt.cache-expire}")
    private int cacheExpire;

    @PostMapping("/register")
    @ApiOperation("注册用户")
    public R register(@Valid @RequestBody RegisterFormVO formVO){
        /*获取注册用户的id*/
        Integer id = userService.registerUser(formVO.getRegisterCode(),
                formVO.getCode(),
                formVO.getNickname(),
                formVO.getPhoto()
        );
        if(id == -1)
            throw new CommonException("注册失败!");
        /*根据用户id生成一个令牌*/
        String token = jwtUtils.createToken(id);
        /*获取该用户权限*/
        Set<String> permissions = userService.searchUserPermissions(id);
        /*存储缓存*/
        saveCacheToken(token,id);
        return R.ok("用户注册成功").put("token",token).put("permission",permissions);
    }

    @PostMapping("/login")
    @ApiOperation("登陆系统")
    public R login(@Valid @RequestBody LoginFormVO loginFormVO){
        String code = loginFormVO.getCode();
        /*获取登陆成员的id*/
        Integer id = userService.login(code);
        /*生成token*/
        String token = jwtUtils.createToken(id);
        /*获取对应权限*/
        Set<String> permissions = userService.searchUserPermissions(id);
        /*存储缓存*/
        //TODO 方便调试 暂时取消缓存
        //saveCacheToken(token,id);
        //TODO ..................
        return R.ok("登录成功").put("token",token).put("permission",permissions);
    }

    @GetMapping("/searchUserSummary")
    @ApiOperation("查询用户摘要信息（用户信息，头像，部门名称）")
    public R searchUserSummary(@RequestHeader("token") String token){
        int userId = jwtUtils.getUserId(token);
        HashMap map = userService.searchUserSummary(userId);
        return R.ok().put("result",map);
    }

    @PostMapping("/searchUserGroupByDept")
    @ApiOperation("查询员工列表，按照部门分组排列")
    @RequiresPermissions(value = {"ROOT","EMPLOYEE:SELECT"},logical = Logical.OR)
    public R searchUserGroupByDept(@Valid @RequestBody SearchUserGroupByDeptFormVO searchUserGroupByDeptFormVO){
        ArrayList<HashMap> list = userService.searchUserGroupByDept(searchUserGroupByDeptFormVO.getKeyword());
        return R.ok().put("result",list);
    }

    @PostMapping("/searchMembers")
    @ApiOperation("查询成员")
    @RequiresPermissions(value = {"ROOT","MEETING:INSERT","MEETING:UPDATE"},logical = Logical.OR)
    public R searchMembers(@RequestBody @Valid SearchMemberFormVO searchMemberFormVO){
        if (!JSONUtil.isJsonArray(searchMemberFormVO.getMembers())){
            throw new CommonException("不是JSON数组");
        }
        ArrayList<Integer> arrayList = JSONUtil.parseArray(searchMemberFormVO.getMembers()).toList(Integer.class);
        List<Map<String, Object>> maps = userService.searchMembers(arrayList);
        return R.ok().put("result",maps);
    }

    @PostMapping("/selectUserPhotoAndName")
    @ApiOperation("查询用户姓名和头像")
    @RequiresPermissions(value = {"WORKFLOW:APPROVAL"})
    public R selectUserPhotoAndName(@Valid @RequestBody SelectUserPhotoAndNameFormVO selectUserPhotoAndNameFormVO){
        if (!JSONUtil.isJsonArray(selectUserPhotoAndNameFormVO.getIds()))
            throw new CommonException("参数不是JSON数组");
        List<Integer> param = JSONUtil.parseArray(selectUserPhotoAndNameFormVO.getIds()).toList(Integer.class);
        List<Map<String, Object>> maps = userService.selectUserPhotoAndName(param);
        return R.ok().put("result",maps);
    }

    private void saveCacheToken(String token, Integer id) {
        /*redis带时间缓存*/
        redisTemplate.opsForValue().set(token,id+"",cacheExpire);
    }
}
