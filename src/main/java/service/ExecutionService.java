package service;


import org.activiti.engine.ActivitiException;
import po.Execution;

import java.util.List;

public interface ExecutionService {
    List<Execution> getExecutionByExecutionId(String executionId) throws ActivitiException;
}
