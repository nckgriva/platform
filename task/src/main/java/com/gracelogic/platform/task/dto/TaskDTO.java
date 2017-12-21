package com.gracelogic.platform.task.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gracelogic.platform.db.dto.IdObjectDTO;
import com.gracelogic.platform.db.dto.JsonDateDeserializer;
import com.gracelogic.platform.db.dto.JsonDateSerializer;
import com.gracelogic.platform.task.model.Task;

import java.util.Date;

public class TaskDTO extends IdObjectDTO {
    private String name;

    private String serviceName;

    private Boolean active;

    private String cronExpression;

    private String parameter;

    private Date lastExecutionDate;

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

    @JsonSerialize(using = JsonDateSerializer.class, include=JsonSerialize.Inclusion.NON_NULL)
    @JsonDeserialize(using = JsonDateDeserializer.class)
    public Date getLastExecutionDate() {
        return lastExecutionDate;
    }

    public void setLastExecutionDate(Date lastExecutionDate) {
        this.lastExecutionDate = lastExecutionDate;
    }

    public static TaskDTO prepare(Task model) {
        TaskDTO taskDTO = new TaskDTO();
        IdObjectDTO.prepare(taskDTO, model);

        taskDTO.setName(model.getName());
        taskDTO.setServiceName(model.getServiceName());
        taskDTO.setCronExpression(model.getCronExpression());
        taskDTO.setParameter(model.getParameter());
        taskDTO.setActive(model.getActive());
        taskDTO.setLastExecutionDate(model.getLastExecutionDate());

        return taskDTO;
    }
}
