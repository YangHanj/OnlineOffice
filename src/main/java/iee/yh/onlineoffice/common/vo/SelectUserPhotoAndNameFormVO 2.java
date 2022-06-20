package iee.yh.onlineoffice.common.vo;

import io.swagger.annotations.ApiModel;

import javax.validation.constraints.NotBlank;

/**
 * @author yanghan
 * @date 2022/5/14
 */
@ApiModel
public class SelectUserPhotoAndNameFormVO {
    @NotBlank
    private String ids;

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }
}
