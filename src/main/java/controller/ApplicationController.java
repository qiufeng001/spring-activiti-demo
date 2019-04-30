package controller;

import com.alibaba.fastjson.JSON;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pagemodel.*;
import po.*;
import service.ApplicationService;
import service.ExecutionService;
import service.LeaveService;
import service.SystemService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

@Controller
public class ApplicationController {
    @Autowired
    RepositoryService rep;
    @Autowired
    RuntimeService runservice;
    @Autowired
    FormService formservice;
    @Autowired
    IdentityService identityservice;
    @Autowired
    LeaveService leaveservice;
    @Autowired
    TaskService taskservice;
    @Autowired
    HistoryService histiryservice;
    @Autowired
    SystemService systemservice;
    @Autowired
    ApplicationService applicationService;
    @Autowired
    ExecutionService executionService;

    // 跳转到管理界面
    @RequestMapping("/applicationManager")
    String applicationManager() {
        return "application/application_manager";
    }

    @RequestMapping("/applicationApply")
    public String leave() {
        return "application/application_apply";
    }

    /**
     * 提交申请，即开启一个任务流程
     */
    @RequestMapping("startApplication")
    @ResponseBody
    String startApplication(@RequestParam("itemlist") String itemlist,
                            @RequestParam("userId") String userId, HttpSession session) {
        String userid = (String) session.getAttribute("username");
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("starter", userid);
        ApplicationApply apply = new ApplicationApply();
        apply.setApplyer(userid);
        apply.setItemlist(itemlist);
        apply.setApplytime(new Date());
        apply.setUserId(userId);
        ProcessInstance ins = applicationService.startWorkflow(apply, userid, variables);
        return JSON.toJSONString("sucess");
    }

    /**
     * 获取任务列表
     *
     * @param session
     * @param current
     * @param rowCount
     * @return
     */
    @RequestMapping("/applicationTaskList")
    @ResponseBody
    DataGrid<ApplicationTask> applicationTaskList(HttpSession session, @RequestParam("current") int current,
                                                  @RequestParam("rowCount") int rowCount) {
        DataGrid<ApplicationTask> grid = new DataGrid<>();
        grid.setRowCount(rowCount);
        grid.setCurrent(current);
        grid.setTotal(0);
        grid.setRows(new ArrayList<>());

        // 获取此人对应的任务列表
        String userid = (String) session.getAttribute("username");
        List<ApplicationApply> list = applicationService.getApplicationIds(userid);
        if(list == null || list.size() <= 0) {
            return grid;
        }

        List<Integer> ids = new ArrayList<>();
        for (ApplicationApply applicationApply : list) {
            ids.add(applicationApply.getId());
        }

        int firstrow = (current - 1) * rowCount;
        List<Task> tasks = applicationService.getTaskList(ids, firstrow, rowCount);
        Integer taskCount = applicationService.getTaskCount(ids);
        if(tasks == null || tasks.size() <= 0) {
            return grid;
        }
        List<ApplicationTask> results = new ArrayList<>();
        for (Task task : tasks) {
            ApplicationTask application = new ApplicationTask();
            String instanceid = task.getProcessInstanceId();
            ProcessInstance ins = runservice.createProcessInstanceQuery().processInstanceId(instanceid).singleResult();
            String businesskey = ins.getBusinessKey();
            ApplicationApply a = applicationService.getApplication(Integer.parseInt(businesskey));
            application.setApplyer(a.getApplyer());
            application.setApplytime(a.getApplytime());
            application.setBussinesskey(a.getId());
            application.setItemlist(a.getItemlist());
            application.setProcessdefid(task.getProcessDefinitionId());
            application.setProcessinstanceid(task.getProcessInstanceId());
            application.setTaskid(task.getId());
            application.setTaskname(task.getName());
            results.add(application);
        }
        grid.setRowCount(rowCount);
        grid.setCurrent(current);
        grid.setTotal((int) taskCount);
        grid.setRows(results);
        return grid;

    }

    /**
     * 处理完毕
     *
     * @param session
     * @param taskid
     * @param req
     * @return
     */
    @RequestMapping("task/applicationComplete/{taskid}")
    @ResponseBody
    public MSG applicationComplete(HttpSession session, @PathVariable("taskid") String taskid, HttpServletRequest req) {
        org.activiti.engine.task.Task task = taskservice.createTaskQuery().taskId(taskid).singleResult();
        ApplicationApply applicationApply = applicationService.getApplictionIdByTaskId(taskid);
        Map<String,Object> variables=new HashMap<String,Object>();
        // 是否同意
        String flag = req.getParameter("manager");
        applicationApply.setUserId(req.getParameter("userId"));
        applicationService.updatePurchase(applicationApply);

        variables.put(task.getTaskDefinitionKey(), flag);
        String userid = (String) session.getAttribute("username");
        taskservice.claim(taskid, userid);
        taskservice.complete(taskid, variables);
        return new MSG("ok");
    }

    /**
     * 跳转到申请历史页面
     *
     * @return
     */
    @RequestMapping("/historyApplication")
    String historyApplication() {
        return "application/application_history";
    }

    // 获取历史数据
    @RequestMapping("getApplicationHistory")
    @ResponseBody
    public DataGrid<ApplicationHistoryProcess> getApplicationHistory(HttpSession session, @RequestParam("current") int current, @RequestParam("rowCount") int rowCount) {
        String userid = (String) session.getAttribute("username");
        HistoricProcessInstanceQuery process = histiryservice.createHistoricProcessInstanceQuery().processDefinitionKey("application").startedBy(userid).finished();
        int total = (int) process.count();
        int firstrow = (current - 1) * rowCount;
        List<HistoricProcessInstance> info = process.listPage(firstrow, rowCount);

        List<ApplicationHistoryProcess> list = new ArrayList<>();
        for (HistoricProcessInstance history : info) {
            ApplicationHistoryProcess his = new ApplicationHistoryProcess();
            String bussinesskey = history.getBusinessKey();
            ApplicationApply apply = applicationService.getApplication(Integer.parseInt(bussinesskey));
            his.setApplicationApply(apply);
            his.setBusinessKey(bussinesskey);
            his.setProcessDefinitionId(history.getProcessDefinitionId());
            list.add(his);
        }
        DataGrid<ApplicationHistoryProcess> grid = new DataGrid<ApplicationHistoryProcess>();
        grid.setCurrent(current);
        grid.setRowCount(rowCount);
        grid.setTotal(total);
        grid.setRows(list);
        return grid;
    }

    @RequestMapping("/hisDetail")
    @ResponseBody
    public List<HistoricActivityInstance> processhis(@RequestParam("ywh") String ywh) {
        String instanceid = histiryservice.createHistoricProcessInstanceQuery().processDefinitionKey("application").processInstanceBusinessKey(ywh).singleResult().getId();
        System.out.println(instanceid);
        List<HistoricActivityInstance> his = histiryservice.createHistoricActivityInstanceQuery().processInstanceId(instanceid).orderByHistoricActivityInstanceStartTime().asc().list();
        return his;
    }

    /**
     * 跳转到我的申请流程页面
     *
     * @return
     */
    @RequestMapping("/myApplication")
    String myApplication() {
        return "application/my_application";
    }

    /**
     * 我发起的申请流程
     */
    @RequestMapping("myApplicationProcess")
    @ResponseBody
    public DataGrid<RunningProcess> myApplicationProcess(HttpSession session, @RequestParam("current") int current, @RequestParam("rowCount") int rowCount) {
        int firstrow = (current - 1) * rowCount;
        String userid = (String) session.getAttribute("username");
        ProcessInstanceQuery query = runservice.createProcessInstanceQuery();
        int total = (int) query.count();
        List<ProcessInstance> a = query.processDefinitionKey("application").involvedUser(userid).listPage(firstrow, rowCount);
        List<RunningProcess> list = new ArrayList<RunningProcess>();
        for (ProcessInstance p : a) {
            RunningProcess process = new RunningProcess();
            if (p.getActivityId() == null) {//有子流程
                String father = p.getProcessInstanceId();
                String sonactiveid = runservice.createExecutionQuery().parentId(father).singleResult().getActivityId();//子流程的活动节点
                String sonexeid = runservice.createExecutionQuery().parentId(father).singleResult().getId();
                process.setActivityid(sonactiveid);
                //System.out.println(taskservice.createTaskQuery().executionId(sonexeid).singleResult().getName());
            } else {
                process.setActivityid(p.getActivityId());
            }
            process.setBusinesskey(p.getBusinessKey());
            process.setExecutionid(p.getId());
            process.setProcessInstanceid(p.getProcessInstanceId());
            ApplicationApply application = applicationService.getApplication(Integer.parseInt(p.getBusinessKey()));
            if (application.getApplyer().equals(userid)) {
                list.add(process);
            } else {
                continue;
            }
        }
        DataGrid<RunningProcess> grid = new DataGrid<RunningProcess>();
        grid.setCurrent(current);
        grid.setRowCount(rowCount);
        grid.setTotal(total);
        grid.setRows(list);
        return grid;
    }
}
