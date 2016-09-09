package com.gracelogic.platform.task.service;

import com.gracelogic.platform.db.service.IdObjectService;
import com.gracelogic.platform.dictionary.service.DictionaryService;
import com.gracelogic.platform.task.DataConstants;
import com.gracelogic.platform.task.model.Task;
import com.gracelogic.platform.task.model.TaskExecuteMethod;
import com.gracelogic.platform.task.model.TaskExecuteState;
import com.gracelogic.platform.task.model.TaskExecutionLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class TaskServiceImpl implements TaskService {
    @Autowired
    private IdObjectService idObjectService;

    @Autowired
    private DictionaryService ds;

    public void executeTask(UUID taskId, String parameter, UUID method) {
        Task task = idObjectService.getObjectById(Task.class, parameter);

        TaskExecutionLog execution = new TaskExecutionLog();
        execution.setTask(task);
        execution.setMethod(ds.get(TaskExecuteMethod.class, method));
        execution.setState(ds.get(TaskExecuteState.class, DataConstants.TaskExecutionStates.CREATED.getValue()));
        execution.setParameter(parameter);

        idObjectService.save(task);
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private void setTaskExecutionState(UUID taskExecutionId, UUID stateId) {

    }

    public void startNextTask() {
        Map<String, Object> params = new HashMap<>();
        params.put("state", DataConstants.TaskExecutionStates.CREATED.getValue());

        List<TaskExecutionLog> executions = idObjectService.getList(TaskExecutionLog.class, null, "el.state=:state",  params, "el.created", "ASC", null, 1);
        if (executions.isEmpty()) {
            return;
        }

        TaskExecutionLog execution = executions.iterator().next();

        setTaskExecutionState(execution.getId(), DataConstants.TaskExecutionStates.IN_PROGRESS.getValue());

        try {
            //Получить класс исполнителя и вызвать у него execute с параметрами
            setTaskExecutionState(execution.getId(), DataConstants.TaskExecutionStates.COMPLETED.getValue());
        }
        catch (Exception e) {
            setTaskExecutionState(execution.getId(), DataConstants.TaskExecutionStates.FAIL.getValue());
        }

        //update task.lastExecutionDate

        idObjectService.save(execution);

    }
}
