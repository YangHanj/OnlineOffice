package iee.yh.onlineoffice.db.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 用户表
 * 
 * @author yanghan
 * @email yanghan1214@163.com
 * @date 2022-04-20 16:15:12
 */
@Data
@TableName("tb_user")
public class UserEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键
	 */
	@TableId(type = IdType.AUTO)
	private Integer id;
	/**
	 * 长期授权字符串
	 */
	private String openId;
	/**
	 * 昵称
	 */
	private String nickname;
	/**
	 * 头像网址
	 */
	private String photo;
	/**
	 * 姓名
	 */
	private String name;
	/**
	 * 性别
	 */
	private String sex;
	/**
	 * 手机号码
	 */
	private String tel;
	/**
	 * 邮箱
	 */
	private String email;
	/**
	 * 入职日期
	 */
	private Date hiredate;
	/**
	 * 角色
	 */
	private Object role;
	/**
	 * 是否是超级管理员
	 */
	private Integer root;
	/**
	 * 部门编号
	 */
	private Integer deptId;
	/**
	 * 状态
	 */
	private Integer status;
	/**
	 * 创建时间
	 */
	private Date createTime;

}
