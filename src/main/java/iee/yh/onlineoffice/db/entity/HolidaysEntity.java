package iee.yh.onlineoffice.db.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 节假日表
 * 
 * @author yanghan
 * @email yanghan1214@163.com
 * @date 2022-04-20 16:15:12
 */
@Data
@TableName("tb_holidays")
public class HolidaysEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键
	 */
	@TableId
	private Integer id;
	/**
	 * 日期
	 */
	private Date date;

}
