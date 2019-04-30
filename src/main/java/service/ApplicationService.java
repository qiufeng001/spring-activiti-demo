package service;

import org.activiti.engine.runtime.ProcessInstance;
import pagemodel.Task;
import po.ApplicationApply;

import java.util.List;
import java.util.Map;

public interface ApplicationService {

    ProcessInstance startWorkflow(ApplicationApply apply, String userid, Map<String, Object> variables);
    ApplicationApply getApplication(int id);
    void updatePurchase(ApplicationApply a);
    List<ApplicationApply> getApplicationIds(String userId);
    List<Task> getTaskList(List<Integer> ids, int pageNumber, int pageSize);
    Integer getTaskCount(List<Integer> ids);
    ApplicationApply getApplictionIdByTaskId(String taskId);
}
