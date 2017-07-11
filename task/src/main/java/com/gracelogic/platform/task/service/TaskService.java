package com.gracelogic.platform.task.service;

import com.gracelogic.platform.db.dto.EntityListResponse;
import com.gracelogic.platform.db.exception.ObjectNotFoundException;
import com.gracelogic.platform.task.dto.TaskDTO;
import com.gracelogic.platform.task.dto.TaskExecutionLogDTO;
import com.gracelogic.platform.task.model.Task;
import com.gracelogic.platform.task.model.TaskExecuteMethod;
import com.gracelogic.platform.task.model.TaskExecuteState;
import com.gracelogic.platform.task.model.TaskExecutionLog;

import java.util.Collection;
import java.util.Date;
import java.util.UUID;

public interface TaskService {
    void executeTask(UUID taskId, String parameter, UUID method);

    void setTaskExecutionState(UUID taskExecutionId, UUID stateId);

    void startNextTask();

    void scheduleCronTasks();

    void updateLastExecutionDate(UUID taskId);

    Task saveTask(TaskDTO dto) throws ObjectNotFoundException;

    TaskDTO getTask(UUID id) throws ObjectNotFoundException;

    void deleteTask(UUID id);

    EntityListResponse<TaskDTO> getTasksPaged(String name, String serviceName, Boolean active, boolean enrich,
                                             Integer count, Integer page, Integer start, String sortField, String sortDir);

    EntityListResponse<TaskExecutionLogDTO> getTelsPaged(UUID task, Collection<UUID> methodIds, Collection<UUID> stateIds, String parameter,
                                                                boolean enrich, Integer count, Integer page, Integer start, String sortField, String sortDir);

    void resetTaskExecution(UUID telId) throws ObjectNotFoundException;
}
