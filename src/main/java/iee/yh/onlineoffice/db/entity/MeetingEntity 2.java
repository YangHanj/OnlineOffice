package iee.yh.onlineoffice.db.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.sql.Time;
import java.util.Date;
import lombok.Data;

/**
 * 会议表
 * 
 * @author yanghan
 * @email yanghan1214@163.com
 * @date 2022-04-20 16:15:12
 */
@Data
@TableName("tb_meeting")
public class MeetingEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键
	 */
	@TableId(type = IdType.AUTO)
	private Long id;
	/**
	 * UUID
	 */
	private String uuid;
	/**
	 * 会议题目
	 */
	private String title;
	/**
	 * 创建人ID
	 */
	private Long creatorId;
	/**
	 * 日期
	 */
	private String date;
	/**
	 * 开会地点
	 */
	private String place;
	/**
	 * 开始时间
	 */
	private String start;
	/**
	 * 结束时间
	 */
	private String end;
	/**
	 * 会议类型（1在线会议，2线下会议）
	 */
	private Short type;
	/**
	 * 参与者
	 */
	private Object members;
	/**
	 * 会议内容
	 */
	@TableField("desc_meet")
	private String desc;
	/**
	 * 工作流实例ID
	 */
	private String instanceId;
	/**
	 * 状态（1待审批，2审批不通过，3未开始，4进行中，5已结束）
	 */
	private Short status;
	/**
	 * 创建时间
	 */
	private Date createTime;

}
