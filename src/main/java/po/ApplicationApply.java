package po;

import org.activiti.engine.task.Task;

import java.util.Date;

public class ApplicationApply {
    int id;
    String itemlist;
    String userId;
    Date applytime;
    String applyer;
    Task task;
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getItemlist() {
        return itemlist;
    }
    public void setItemlist(String itemlist) {
        this.itemlist = itemlist;
    }
    public Date getApplytime() {
        return applytime;
    }
    public void setApplytime(Date applytime) {
        this.applytime = applytime;
    }
    public String getApplyer() {
        return applyer;
    }
    public void setApplyer(String applyer) {
        this.applyer = applyer;
    }
    public Task getTask() {
        return task;
    }
    public void setTask(Task task) {
        this.task = task;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
