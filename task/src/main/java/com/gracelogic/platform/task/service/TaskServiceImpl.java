package com.gracelogic.platform.task.service;

import com.gracelogic.platform.db.service.IdObjectService;
import com.gracelogic.platform.dictionary.service.DictionaryService;
import com.gracelogic.platform.task.DataConstants;
import com.gracelogic.platform.task.model.Task;
import com.gracelogic.platform.task.model.TaskExecuteMethod;
import com.gracelogic.platform.task.model.TaskExecuteState;
import com.gracelogic.platform.task.model.TaskExecutionLog;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service("taskService")
public class TaskServiceImpl implements TaskService {
    private static Logger logger = Logger.getLogger(TaskServiceImpl.class);

    @Autowired
    private IdObjectService idObjectService;

    @Autowired
    private DictionaryService ds;

    @Autowired
    private ApplicationContext applicationContext;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void executeTask(UUID taskId, String parameter, UUID method) {
        if (checkTaskExist(taskId, parameter)) {
            logger.debug("Task already exist - skip it");
            return;
        }

        Task task = idObjectService.getObjectById(Task.class, taskId);

        TaskExecutionLog execution = new TaskExecutionLog();
        execution.setTask(task);
        execution.setMethod(ds.get(TaskExecuteMethod.class, method));
        execution.setState(ds.get(TaskExecuteState.class, DataConstants.TaskExecutionStates.CREATED.getValue()));
        execution.setParameter(parameter);

        idObjectService.save(execution);
    }

    protected boolean checkTaskExist(UUID taskId, String parameter) {
        Map<String, Object> params = new HashMap<>();
        params.put("taskId", taskId);
        params.put("parameter", parameter);
        params.put("stateId", DataConstants.TaskExecutionStates.CREATED.getValue());
        Integer count = idObjectService.checkExist(TaskExecutionLog.class, null, "el.task.id=:taskId and el.parameter=:parameter and el.state.id=:stateId", params, 1);
        return count > 0;
    }

    protected void setTaskExecutionStateInOtherTransaction(UUID taskExecutionId, UUID stateId) {
        TaskService taskService = applicationContext.getBean("taskService", TaskService.class);
        taskService.setTaskExecutionState(taskExecutionId, stateId);
    }

    protected void updateLastExecutionDateInOtherTransaction(UUID taskId) {
        TaskService taskService = applicationContext.getBean("taskService", TaskService.class);
        taskService.updateLastExecutionDate(taskId);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void updateLastExecutionDate(UUID taskId) {
        idObjectService.updateFieldValue(Task.class, taskId, "lastExecutionDate", new Date());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void setTaskExecutionState(UUID taskExecutionId, UUID stateId) {
        idObjectService.updateFieldValue(TaskExecutionLog.class, taskExecutionId, "state.id", stateId);
    }

    @Override
    public void startNextTask() {
        Map<String, Object> params = new HashMap<>();
        params.put("state", DataConstants.TaskExecutionStates.CREATED.getValue());

        List<TaskExecutionLog> executions = idObjectService.getList(TaskExecutionLog.class, null, "el.state.id=:state",  params, "el.created", "ASC", null, 1);
        if (executions.isEmpty()) {
            return;
        }

        TaskExecutionLog execution = executions.iterator().next();

        setTaskExecutionStateInOtherTransaction(execution.getId(), DataConstants.TaskExecutionStates.IN_PROGRESS.getValue());

        try {
            TaskExecutor executor = applicationContext.getBean(execution.getTask().getServiceName(), TaskExecutor.class);
            executor.execute(execution.getParameter());

            //Получить класс исполнителя и вызвать у него execute с параметрами
            setTaskExecutionStateInOtherTransaction(execution.getId(), DataConstants.TaskExecutionStates.COMPLETED.getValue());
        }
        catch (Exception e) {
            setTaskExecutionStateInOtherTransaction(execution.getId(), DataConstants.TaskExecutionStates.FAIL.getValue());
            logger.error(String.format("Failed to complete task: %s", execution.getTask().getServiceName()), e);
        }

        updateLastExecutionDateInOtherTransaction(execution.getTask().getId());

        idObjectService.save(execution);
    }
}
