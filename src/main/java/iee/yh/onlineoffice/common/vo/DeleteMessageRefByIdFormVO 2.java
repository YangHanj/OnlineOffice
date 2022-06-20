package iee.yh.onlineoffice.common.vo;

import io.swagger.annotations.Api;

import javax.validation.constraints.NotBlank;

/**
 * @author yanghan
 * @date 2022/5/8
 */
@Api
public class DeleteMessageRefByIdFormVO {
    @NotBlank
    private String Id;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }
}
