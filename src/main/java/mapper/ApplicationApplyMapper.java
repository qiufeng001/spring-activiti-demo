package mapper;

import org.apache.ibatis.annotations.Param;
import pagemodel.Task;
import po.ApplicationApply;

import java.util.List;

public interface ApplicationApplyMapper {
    void save(ApplicationApply apply);
    ApplicationApply get(int id);
    void update(ApplicationApply app);
    List<ApplicationApply> getApplicationIds(String userId);
    List<Task> getTaskList(@Param("ids") List<Integer> ids, @Param("pageNumber") int pageNumber, @Param("pageSize") int pageSize);
    Integer getTaskCount(@Param("ids") List<Integer> ids);
    ApplicationApply getApplictionIdByTaskId(String taskId);
}
