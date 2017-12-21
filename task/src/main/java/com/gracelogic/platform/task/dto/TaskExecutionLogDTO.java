package com.gracelogic.platform.task.dto;

import com.gracelogic.platform.db.dto.IdObjectDTO;
import com.gracelogic.platform.task.model.TaskExecutionLog;

import java.util.UUID;

public class TaskExecutionLogDTO extends IdObjectDTO {
    private UUID taskId;

    private String taskName;

    private UUID methodId;

    private String methodName;

    private UUID stateId;

    private String stateName;

    private String parameter;

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

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public static TaskExecutionLogDTO prepare(TaskExecutionLog model) {
        TaskExecutionLogDTO dto = new TaskExecutionLogDTO();
        IdObjectDTO.prepare(dto, model);

        dto.setParameter(model.getParameter());
        if (model.getTask() != null) {
            dto.setTaskId(model.getTask().getId());
        }
        if (model.getMethod() != null) {
            dto.setMethodId(model.getMethod().getId());
        }
        if (model.getState() != null) {
            dto.setStateId(model.getState().getId());
        }

        return dto;
    }

    public static void enrich(TaskExecutionLogDTO dto, TaskExecutionLog model) {
        if (model.getTask() != null) {
            dto.setTaskName(model.getTask().getName());
        }
        if (model.getMethod() != null) {
            dto.setMethodName(model.getMethod().getName());
        }
        if (model.getState() != null) {
            dto.setStateName(model.getState().getName());
        }
    }
}
