package iee.yh.onlineoffice.common.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.apache.ibatis.annotations.Param;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * 用户注册需要传入的信息
 * @author yanghan
 * @date 2022/4/23
 */
@ApiModel
@Data
public class RegisterFormVO {
    @NotBlank(message = "注册码不能为空")
    @Pattern(regexp = "^[0-9]{6}$",message = "注册码必须是6位数字")
    private String registerCode;

    @NotBlank(message = "微信临时授权不能为空")
    private String code;

    @NotBlank(message = "昵称不能为空")
    private String nickname;

    @NotBlank(message = "头像不能为空")
    private String photo;
}
