package com.gracelogic.platform.task.service;

import com.gracelogic.platform.db.dto.EntityListResponse;
import com.gracelogic.platform.db.exception.ObjectNotFoundException;
import com.gracelogic.platform.task.dto.TaskDTO;
import com.gracelogic.platform.task.dto.TaskExecutionLogDTO;
import com.gracelogic.platform.task.model.Task;

import java.util.Collection;
import java.util.Date;
import java.util.UUID;

public interface TaskService {
    void executeTask(Task task, String parameter, UUID method);

    void setTaskExecutionState(UUID taskExecutionId, UUID stateId);

    void startNextTask();

    void scheduleCronTasks();

    void updateLastExecutionDate(UUID taskId);

    Task saveTask(TaskDTO dto) throws ObjectNotFoundException;

    TaskDTO getTask(UUID id) throws ObjectNotFoundException;

    void deleteTask(UUID id);

    EntityListResponse<TaskDTO> getTasksPaged(String name, String serviceName, Boolean active, boolean enrich,
                                             Integer count, Integer page, Integer start, String sortField, String sortDir, boolean calculate);

    EntityListResponse<TaskExecutionLogDTO> getTaskExecutionLogsPaged(UUID task, Collection<UUID> methodIds, Collection<UUID> stateIds, String parameter, Date startDate, Date endDate,
                                                                      boolean enrich, Integer count, Integer page, Integer start, String sortField, String sortDir, boolean calculate);

    void resetTaskExecution(UUID telId) throws ObjectNotFoundException;

    void resetAllTasks();
}
