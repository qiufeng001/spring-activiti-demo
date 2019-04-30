package service.impl;

import mapper.ApplicationApplyMapper;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pagemodel.ApplicationTask;
import pagemodel.Task;
import po.ApplicationApply;
import service.ApplicationService;

import java.util.List;
import java.util.Map;

@Service
public class ApplicationServiceImpl implements ApplicationService {

    @Autowired
    ApplicationApplyMapper mapper;
    @Autowired
    IdentityService identityservice;
    @Autowired
    RuntimeService runtimeservice;
    @Autowired
    TaskService taskservice;


    @Override
    public ProcessInstance startWorkflow(ApplicationApply apply, String userid, Map<String, Object> variables) {
        try {
            mapper.save(apply);
            String businesskey = String.valueOf(apply.getId());//使用leaveapply表的主键作为businesskey,连接业务数据和流程数据
            Authentication.setAuthenticatedUserId(userid);
            ProcessInstance instance = runtimeservice.startProcessInstanceByKey("application", businesskey, variables);
            return instance;
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }


    }

    @Override
    public ApplicationApply getApplication(int id) {
        return mapper.get(id);
    }

    @Override
    public void updatePurchase(ApplicationApply apply) {
        mapper.update(apply);
    }

    @Override
    public List<ApplicationApply> getApplicationIds(String userId) {
        return mapper.getApplicationIds(userId);
    }

    @Override
    public List<Task> getTaskList(List<Integer> ids, int pageNumber, int pageSize) {
        return mapper.getTaskList(ids, pageNumber, pageSize);
    }

    @Override
    public Integer getTaskCount(List<Integer> ids) {
        return mapper.getTaskCount(ids);
    }

    @Override
    public ApplicationApply getApplictionIdByTaskId(String taskId) {
        return mapper.getApplictionIdByTaskId(taskId);
    }
}
