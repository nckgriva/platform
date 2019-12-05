package com.gracelogic.platform.task.service;

import com.gracelogic.platform.db.dto.EntityListResponse;
import com.gracelogic.platform.db.exception.ObjectNotFoundException;
import com.gracelogic.platform.db.service.IdObjectService;
import com.gracelogic.platform.dictionary.service.DictionaryService;
import com.gracelogic.platform.task.DataConstants;
import com.gracelogic.platform.task.dao.TaskDao;
import com.gracelogic.platform.task.dto.TaskDTO;
import com.gracelogic.platform.task.dto.TaskExecutionLogDTO;
import com.gracelogic.platform.task.model.Task;
import com.gracelogic.platform.task.model.TaskExecuteMethod;
import com.gracelogic.platform.task.model.TaskExecuteState;
import com.gracelogic.platform.task.model.TaskExecutionLog;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.quartz.CronExpression;
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

    @Autowired
    private TaskDao taskDao;

    @Autowired
    private TaskService taskService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void executeTask(Task task, String parameter, UUID method) {
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
        return idObjectService.checkExist(TaskExecutionLog.class, null, "el.task.id=:taskId and el.parameter=:parameter and el.state.id=:stateId", params, 1) > 0;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateLastExecutionDate(UUID taskId) {
        idObjectService.updateFieldValue(Task.class, taskId, "lastExecutionDate", new Date());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void setTaskExecutionState(UUID taskExecutionId, UUID stateId) {
        idObjectService.updateFieldValue(TaskExecutionLog.class, taskExecutionId, "state.id", stateId);
    }

    @Override
    public void startNextTask() {
        Map<String, Object> params = new HashMap<>();
        params.put("state", DataConstants.TaskExecutionStates.CREATED.getValue());

        List<TaskExecutionLog> executions = idObjectService.getList(TaskExecutionLog.class, "left join fetch el.task", "el.state.id=:state", params, "el.created", "ASC", null, 1);
        if (executions.isEmpty()) {
            return;
        }

        TaskExecutionLog execution = executions.iterator().next();

        taskService.setTaskExecutionState(execution.getId(), DataConstants.TaskExecutionStates.IN_PROGRESS.getValue());

        try {
            TaskExecutor executor = applicationContext.getBean(execution.getTask().getServiceName(), TaskExecutor.class);
            executor.execute(execution.getParameter());

            taskService.setTaskExecutionState(execution.getId(), DataConstants.TaskExecutionStates.COMPLETED.getValue());
        } catch (Exception e) {
            logger.error(String.format("Failed to complete task: %s", execution.getTask().getServiceName()), e);
            taskService.setTaskExecutionState(execution.getId(), DataConstants.TaskExecutionStates.FAIL.getValue());
        }

        taskService.updateLastExecutionDate(execution.getTask().getId());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void scheduleCronTasks() {
        Date currentDate = new Date();
        List<Task> tasks = idObjectService.getList(Task.class, null, "el.active=true and el.cronExpression is not null", null, "el.lastExecutionDate", "ASC", null, null);

        for (Task task : tasks) {
            try {
                if (!StringUtils.isEmpty(task.getCronExpression()) && !checkTaskExist(task.getId(), task.getParameter())) {
                    Date nextExecutionDate;
                    if (task.getLastExecutionDate() != null) {
                        CronExpression cronExpression = new CronExpression(task.getCronExpression());
                        nextExecutionDate = cronExpression.getNextValidTimeAfter(task.getLastExecutionDate());
                    }
                    else {
                        nextExecutionDate = currentDate;
                    }

                    if (nextExecutionDate.getTime() <= currentDate.getTime()) {
                        executeTask(task, task.getParameter(), DataConstants.TaskExecutionMethods.CRON.getValue());
                    }
                }
            } catch (Exception e) {
                logger.error(String.format("Failed to schedule task %s", task.getId()), e);
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void resetAllTasks() {
        taskDao.resetAllTasks();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void resetTaskExecution(UUID taskExecutionLogId) throws ObjectNotFoundException {
        TaskExecutionLog entity = idObjectService.getObjectById(TaskExecutionLog.class, taskExecutionLogId);
        if (entity == null) {
            throw new ObjectNotFoundException();
        }

        if (entity.getState().getId().equals(DataConstants.TaskExecutionStates.CREATED.getValue()) || entity.getState().getId().equals(DataConstants.TaskExecutionStates.IN_PROGRESS.getValue())) {
            entity.setState(ds.get(TaskExecuteState.class, DataConstants.TaskExecutionStates.FAIL.getValue()));
            idObjectService.save(entity);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Task saveTask(TaskDTO dto) throws ObjectNotFoundException {
        Task entity;
        if (dto.getId() != null) {
            entity = idObjectService.getObjectById(Task.class, dto.getId());
            if (entity == null) {
                throw new ObjectNotFoundException();
            }
        } else {
            entity = new Task();
        }

        entity.setName(dto.getName());
        entity.setServiceName(dto.getServiceName());
        entity.setCronExpression(dto.getCronExpression());
        entity.setParameter(dto.getParameter());
        entity.setActive(dto.getActive());
        entity.setLastExecutionDate(dto.getLastExecutionDate());

        return idObjectService.save(entity);
    }

    @Override
    public TaskDTO getTask(UUID id) throws ObjectNotFoundException {
        Task entity = idObjectService.getObjectById(Task.class, id);
        if (entity == null) {
            throw new ObjectNotFoundException();
        }

        return TaskDTO.prepare(entity);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteTask(UUID id) {
        idObjectService.delete(Task.class, id);
    }

    @Override
    public EntityListResponse<TaskDTO> getTasksPaged(String name, String serviceName, Boolean active, boolean enrich,
                                                     Integer count, Integer page, Integer start, String sortField, String sortDir) {
        String fetches = "";
        String countFetches = "";
        String cause = "1=1 ";
        HashMap<String, Object> params = new HashMap<String, Object>();

        if (!StringUtils.isEmpty(name)) {
            params.put("name", "%%" + StringUtils.lowerCase(name) + "%%");
            cause += " and lower(el.name) like :name";
        }

        if (!StringUtils.isEmpty(serviceName)) {
            params.put("serviceName", "%%" + StringUtils.lowerCase(serviceName) + "%%");
            cause += " and lower(el.serviceName) like :serviceName";
        }

        if (active != null) {
            cause += " and el.active = :active";
            params.put("active", active);
        }

        int totalCount = idObjectService.getCount(Task.class, null, countFetches, cause, params);

        EntityListResponse<TaskDTO> entityListResponse = new EntityListResponse<TaskDTO>(totalCount, count, page, start);

        List<Task> items = idObjectService.getList(Task.class, fetches, cause, params, sortField, sortDir, entityListResponse.getStartRecord(), count);
        for (Task e : items) {
            TaskDTO el = TaskDTO.prepare(e);
            entityListResponse.addData(el);
        }

        return entityListResponse;
    }

    @Override
    public EntityListResponse<TaskExecutionLogDTO> getTaskExecutionLogsPaged(UUID taskId, Collection<UUID> methodIds, Collection<UUID> stateIds, String parameter, Date startDate, Date endDate,
                                                                             boolean enrich, Integer count, Integer page, Integer start, String sortField, String sortDir) {
        String fetches = enrich ? "left join fetch el.task left join fetch el.method left join fetch el.state" : "";
        String countFetches = "";
        String cause = "1=1 ";
        HashMap<String, Object> params = new HashMap<String, Object>();

        if (taskId != null) {
            cause += "and el.task.id = :taskId ";
            params.put("taskId", taskId);
        }

        if (methodIds != null && !methodIds.isEmpty()) {
            cause += "and el.method.id in (:methodIds) ";
            params.put("methodIds", methodIds);
        }

        if (stateIds != null && !stateIds.isEmpty()) {
            cause += "and el.state.id in (:stateIds) ";
            params.put("stateIds", stateIds);
        }

        if (!StringUtils.isEmpty(parameter)) {
            params.put("parameter", "%%" + StringUtils.lowerCase(parameter) + "%%");
            cause += "and lower(el.parameter) like :parameter";
        }
        if (startDate != null) {
            cause += "and el.created >= :startDate ";
            params.put("startDate", startDate);
        }
        if (endDate != null) {
            cause += "and el.created <= :endDate ";
            params.put("endDate", endDate);
        }

        int totalCount = idObjectService.getCount(TaskExecutionLog.class, null, countFetches, cause, params);

        EntityListResponse<TaskExecutionLogDTO> entityListResponse = new EntityListResponse<TaskExecutionLogDTO>(totalCount, count, page, start);

        List<TaskExecutionLog> items = idObjectService.getList(TaskExecutionLog.class, fetches, cause, params, sortField, sortDir, entityListResponse.getStartRecord(), count);
        for (TaskExecutionLog e : items) {
            TaskExecutionLogDTO el = TaskExecutionLogDTO.prepare(e);
            if (enrich) {
                TaskExecutionLogDTO.enrich(el, e);
            }
            entityListResponse.addData(el);
        }

        return entityListResponse;
    }
}
