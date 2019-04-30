package pagemodel;

import po.ApplicationApply;
import po.LeaveApply;
import po.PurchaseApply;

public class ApplicationHistoryProcess {
    String processDefinitionId;
    String businessKey;
    LeaveApply leaveapply;
    ApplicationApply applicationApply;
    public String getProcessDefinitionId() {
        return processDefinitionId;
    }
    public void setProcessDefinitionId(String processDefinitionId) {
        this.processDefinitionId = processDefinitionId;
    }
    public String getBusinessKey() {
        return businessKey;
    }
    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }
    public LeaveApply getLeaveapply() {
        return leaveapply;
    }
    public void setLeaveapply(LeaveApply leaveapply) {
        this.leaveapply = leaveapply;
    }
    public ApplicationApply getApplicationApply() {
        return applicationApply;
    }
    public void setApplicationApply(ApplicationApply applicationApply) {
        this.applicationApply = applicationApply;
    }

}
