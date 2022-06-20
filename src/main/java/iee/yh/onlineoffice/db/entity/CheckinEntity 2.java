package iee.yh.onlineoffice.db.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 签到表
 * 
 * @author yanghan
 * @email yanghan1214@163.com
 * @date 2022-04-20 16:15:12
 */
@Data
@TableName("tb_checkin")
public class CheckinEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键
	 */
	@TableId(type = IdType.AUTO)
	private Integer id;
	/**
	 * 用户ID
	 */
	private Integer userId;
	/**
	 * 签到地址
	 */
	private String address;
	/**
	 * 国家
	 */
	private String country;
	/**
	 * 省份
	 */
	private String province;
	/**
	 * 城市
	 */
	private String city;
	/**
	 * 区划
	 */
	private String district;
	/**
	 * 考勤结果
	 */
	private Integer status;
	/**
	 * 风险等级
	 */
	private Integer risk;
	/**
	 * 签到日期
	 */
	private String date;
	/**
	 * 签到时间
	 */
	private Date createTime;

}
