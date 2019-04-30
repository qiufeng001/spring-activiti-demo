package mapper;

import po.Execution;

import java.util.List;

public interface ExecutionMapper {
    List<Execution> getExecutionByExecutionId(String executionId);
}
