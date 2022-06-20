package iee.yh.onlineoffice.common.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 用户登录所需要传入的临时授权码
 * @author yanghan
 * @date 2022/4/26
 */
@ApiModel
public class LoginFormVO {
    @NotBlank(message = "临时授权码不能为空")
    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
