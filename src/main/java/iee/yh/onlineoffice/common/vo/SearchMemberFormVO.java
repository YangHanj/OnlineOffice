package iee.yh.onlineoffice.common.vo;

import io.swagger.annotations.ApiModel;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @author yanghan
 * @date 2022/5/12
 */
@ApiModel
public class SearchMemberFormVO {
    @NotBlank
    private String members;

    public String getMembers() {
        return members;
    }

    public void setMembers(String members) {
        this.members = members;
    }
}
