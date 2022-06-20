package iee.yh.onlineoffice.db.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 疫情城市列表
 * 
 * @author yanghan
 * @email yanghan1214@163.com
 * @date 2022-04-20 16:15:12
 */
@Data
@TableName("tb_city")
public class CityEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键
	 */
	@TableId(type = IdType.AUTO)
	private Integer id;
	/**
	 * 城市名称
	 */
	private String city;
	/**
	 * 拼音简称
	 */
	private String code;

}
