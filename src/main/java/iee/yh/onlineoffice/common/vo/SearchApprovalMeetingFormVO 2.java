package iee.yh.onlineoffice.common.vo;

import io.swagger.annotations.ApiModel;

import javax.validation.constraints.NotBlank;

/**
 * @author yanghan
 * @date 2022/5/13
 */
@ApiModel
public class SearchApprovalMeetingFormVO extends SearchMyMeetingListPageFormVO{
    @NotBlank
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
