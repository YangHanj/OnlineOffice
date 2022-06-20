package iee.yh.onlineoffice.common.vo;

import io.swagger.annotations.ApiModel;

import javax.validation.constraints.NotBlank;

/**
 * @author yanghan
 * @date 2022/5/8
 */
@ApiModel
public class SearchMessageByIdFormVO {
    @NotBlank
    private String Id;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }
}
