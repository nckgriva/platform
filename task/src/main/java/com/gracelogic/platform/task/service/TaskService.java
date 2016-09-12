package com.gracelogic.platform.task.service;

import java.util.UUID;

public interface TaskService {
    void executeTask(UUID taskId, String parameter, UUID method);

    void setTaskExecutionState(UUID taskExecutionId, UUID stateId);

    void startNextTask();
}
