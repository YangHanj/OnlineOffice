package iee.yh.onlineoffice.common.vo;

import io.swagger.annotations.ApiModel;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 分页消息查询
 * @author yanghan
 * @date 2022/5/8
 */
@ApiModel
public class SearchMessageByPageFormVO {
    @NotNull
    @Min(1)
    private Integer page;
    @NotNull
    @Range(min = 1,max = 40)
    private Integer length;

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }
}
