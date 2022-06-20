package iee.yh.onlineoffice.common.vo;

import io.swagger.annotations.ApiModel;

import javax.validation.constraints.NotBlank;

/**
 * @author yanghan
 * @date 2022/5/14
 */
@ApiModel
public class approvalMeetingFormVO {
    @NotBlank
    private String taskId;
    @NotBlank
    private String processType;
    @NotBlank
    private String approval;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getProcessType() {
        return processType;
    }

    public void setProcessType(String processType) {
        this.processType = processType;
    }

    public String getApproval() {
        return approval;
    }

    public void setApproval(String approval) {
        this.approval = approval;
    }
}
