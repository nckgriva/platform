package com.gracelogic.platform.task.dto;

import com.gracelogic.platform.db.dto.IdObjectDTO;
import com.gracelogic.platform.task.model.TaskExecutionLog;

import java.util.Date;
import java.util.UUID;

public class TaskExecutionLogDTO extends IdObjectDTO {
    private UUID id;

    private Date created;

    private Date changed;

    private UUID taskId;

    private String taskName;

    private UUID methodId;

    private String methodName;

    private UUID stateId;

    private String stateName;

    private String parameter;

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public void setId(UUID id) {
        this.id = id;
    }

    @Override
    public Date getCreated() {
        return created;
    }

    @Override
    public void setCreated(Date created) { this.created = created; }

    @Override
    public Date getChanged() {
        return changed;
    }

    @Override
    public void setChanged(Date changed) {
        this.changed = changed;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public UUID getTaskId() {
        return taskId;
    }

    public void setTaskId(UUID taskId) {
        this.taskId = taskId;
    }

    public UUID getMethodId() {
        return methodId;
    }

    public void setMethodId(UUID methodId) {
        this.methodId = methodId;
    }

    public UUID getStateId() {
        return stateId;
    }

    public void setStateId(UUID stateId) {
        this.stateId = stateId;
    }

    public String getTaskName() { return taskName; }

    public void setTaskName(String taskName) { this.taskName = taskName; }

    public String getMethodName() { return methodName; }

    public void setMethodName(String methodName) { this.methodName = methodName; }

    public String getStateName() { return stateName; }

    public void setStateName(String stateName) { this.stateName = stateName; }

    public static TaskExecutionLogDTO prepare(TaskExecutionLog tel) {

        TaskExecutionLogDTO dto = new TaskExecutionLogDTO();
        IdObjectDTO.prepare(dto, tel);

        dto.setParameter(tel.getParameter());
        if (tel.getTask() != null) {
            dto.setTaskId(tel.getTask().getId());
        }
        if (tel.getMethod() != null) {
            dto.setMethodId(tel.getMethod().getId());
        }
        if (tel.getState() != null) {
            dto.setStateId(tel.getState().getId());
        }

        return dto;
    }

    public static TaskExecutionLogDTO enrich(TaskExecutionLogDTO dto, TaskExecutionLog tel) {
        if (tel.getTask() != null) {
            dto.setTaskName(tel.getTask().getName());
        }
        if (tel.getMethod() != null) {
            dto.setMethodName(tel.getMethod().getName());
        }
        if (tel.getState() != null) {
            dto.setStateName(tel.getState().getName());
        }

        return dto;
    }
}
