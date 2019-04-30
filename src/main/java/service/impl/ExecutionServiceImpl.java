package service.impl;

import mapper.ExecutionMapper;
import org.activiti.engine.ActivitiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import po.Execution;
import service.ExecutionService;

import java.util.List;

@Service
public class ExecutionServiceImpl implements ExecutionService {

    @Autowired
    private ExecutionMapper mapper;

    @Override
    public List<Execution> getExecutionByExecutionId(String executionId) throws ActivitiException {
        return mapper.getExecutionByExecutionId(executionId);
    }
}
