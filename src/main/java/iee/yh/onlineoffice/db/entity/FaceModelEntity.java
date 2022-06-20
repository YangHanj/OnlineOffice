package iee.yh.onlineoffice.db.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * 
 * @author yanghan
 * @email yanghan1214@163.com
 * @date 2022-04-20 16:15:12
 */
@Data
@TableName("tb_face_model")
public class FaceModelEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键值
	 */
	@TableId(type = IdType.AUTO)
	private Integer id;
	/**
	 * 用户ID
	 */
	private Integer userId;
	/**
	 * 用户人脸模型
	 */
	private String faceModel;

}
