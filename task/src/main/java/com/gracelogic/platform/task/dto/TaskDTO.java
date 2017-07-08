package com.gracelogic.platform.task.dto;

import com.gracelogic.platform.db.dto.IdObjectDTO;
import com.gracelogic.platform.task.model.Task;

import java.util.Date;
import java.util.UUID;

public class TaskDTO extends IdObjectDTO {

    private UUID id;

    private Date created;

    private Date changed;

    private String name;

    private String serviceName;

    private Boolean active;

    private String cronExpression;

    private String parameter;

    private Date lastExecutionDate;

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public void setId(UUID id) {
        this.id = id;
    }

    @Override
    public Date getChanged() {
        return changed;
    }

    @Override
    public void setChanged(Date changed) {
        this.changed = changed;
    }

    @Override
    public Date getCreated() {
        return created;
    }

    @Override
    public void setCreated(Date created) {
        this.created = created;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Date getLastExecutionDate() {
        return lastExecutionDate;
    }

    public void setLastExecutionDate(Date lastExecutionDate) {
        this.lastExecutionDate = lastExecutionDate;
    }

    public static TaskDTO prepare(Task task) {
        TaskDTO taskDTO = new TaskDTO();
        IdObjectDTO.prepare(taskDTO, task);

        taskDTO.setName(task.getName());
        taskDTO.setServiceName(task.getServiceName());
        taskDTO.setCronExpression(task.getCronExpression());
        taskDTO.setParameter(task.getParameter());
        taskDTO.setActive(task.getActive());
        taskDTO.setLastExecutionDate(task.getLastExecutionDate());

        return taskDTO;
    }
}
