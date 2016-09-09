package com.gracelogic.platform.task.model;


import com.gracelogic.platform.db.JPAProperties;
import com.gracelogic.platform.db.model.IdObject;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

/**
 * Author: Igor Parkhomenko
 * Date: 11.12.14
 * Time: 12:34
 */
@Entity
@Table(name = JPAProperties.TABLE_PREFIX + "TASK_EXECUTION_LOG", schema = JPAProperties.DEFAULT_SCHEMA)
public class TaskExecutionLog extends IdObject<UUID> {
    @Id
    @Column(name = ID)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @org.hibernate.annotations.Type(type = "pg-uuid")
    @Access(AccessType.PROPERTY)
    private UUID id;

    @Column(name = CREATED, nullable = false)
    private Date created;

    @Version
    @Column(name = CHANGED, nullable = false)
    private Date changed;

    @ManyToOne
    @JoinColumn(name = "TASK_ID", nullable = false)
    private Task task;

    @ManyToOne
    @JoinColumn(name = "METHOD_ID", nullable = false)
    private TaskExecuteMethod method;

    @ManyToOne
    @JoinColumn(name = "STATE_ID", nullable = false)
    private TaskExecuteState state;

    @Column(name = "PARAMETER", nullable = false)
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
    public void setCreated(Date created) {
        this.created = created;
    }

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

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public TaskExecuteMethod getMethod() {
        return method;
    }

    public void setMethod(TaskExecuteMethod method) {
        this.method = method;
    }

    public TaskExecuteState getState() {
        return state;
    }

    public void setState(TaskExecuteState state) {
        this.state = state;
    }
}
