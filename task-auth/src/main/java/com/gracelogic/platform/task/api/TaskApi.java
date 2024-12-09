package com.gracelogic.platform.task.api;

import com.gracelogic.platform.db.dto.DateFormatConstants;
import com.gracelogic.platform.db.dto.EntityListResponse;
import com.gracelogic.platform.db.exception.ObjectNotFoundException;
import com.gracelogic.platform.localization.service.LocaleHolder;
import com.gracelogic.platform.task.Path;
import com.gracelogic.platform.task.dto.TaskDTO;
import com.gracelogic.platform.task.dto.TaskExecutionLogDTO;
import com.gracelogic.platform.task.model.Task;
import com.gracelogic.platform.task.service.TaskService;
import com.gracelogic.platform.user.api.AbstractAuthorizedController;
import com.gracelogic.platform.web.dto.EmptyResponse;
import com.gracelogic.platform.web.dto.ErrorResponse;
import com.gracelogic.platform.web.dto.IDResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Date;
import java.util.UUID;

@Controller
@RequestMapping(value = Path.API_TASK)
public class TaskApi extends AbstractAuthorizedController {
    @Autowired
    @Qualifier("dbMessageSource")
    private ResourceBundleMessageSource messageSource;

    @Autowired
    private  TaskService taskService;

    @PreAuthorize("hasAuthority('TASK:SHOW')")
    @RequestMapping(method = RequestMethod.GET, value = "/task-execution-log")
    @ResponseBody
    public ResponseEntity getTaskExecutionLogs(@RequestParam(value = "taskId", required = false) UUID taskId,
                                               @RequestParam(value = "methodId", required = false) UUID methodId,
                                               @RequestParam(value = "stateId", required = false) UUID stateId,
                                               @RequestParam(value = "parameter", required = false) String parameter,
                                               @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date startDate,
                                               @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date endDate,
                                               @RequestParam(value = "enrich", required = false, defaultValue = "false") Boolean enrich,
                                               @RequestParam(value = "calculate", required = false, defaultValue = "false") Boolean calculate,
                                               @RequestParam(value = "start", required = false, defaultValue = "0") Integer start,
                                               @RequestParam(value = "count", required = false, defaultValue = "10") Integer count,
                                               @RequestParam(value = "sortField", required = false, defaultValue = "el.created") String sortField,
                                               @RequestParam(value = "sortDir", required = false, defaultValue = "desc") String sortDir) {

        EntityListResponse<TaskExecutionLogDTO> tels =
                taskService.getTaskExecutionLogsPaged(taskId, methodId != null ? Collections.singletonList(methodId) : null, stateId != null ? Collections.singletonList(stateId) : null, parameter, startDate,
                        endDate, enrich, calculate, count, null, start, sortField, sortDir);
        return new ResponseEntity<EntityListResponse<TaskExecutionLogDTO>>(tels, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('TASK:SHOW')")
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity getTasks(@RequestParam(value = "name", required = false) String name,
                                   @RequestParam(value = "serviceName", required = false) String serviceName,
                                   @RequestParam(value = "active", required = false) Boolean active,
                                   @RequestParam(value = "enrich", required = false, defaultValue = "false") Boolean enrich,
                                   @RequestParam(value = "calculate", required = false, defaultValue = "false") Boolean calculate,
                                   @RequestParam(value = "start", required = false, defaultValue = "0") Integer start,
                                   @RequestParam(value = "count", required = false, defaultValue = "10") Integer count,
                                   @RequestParam(value = "sortField", required = false, defaultValue = "el.created") String sortField,
                                   @RequestParam(value = "sortDir", required = false, defaultValue = "desc") String sortDir) {

        EntityListResponse<TaskDTO> tasks = taskService.getTasksPaged(name, serviceName, active, enrich, calculate, count, null, start, sortField, sortDir);
        return new ResponseEntity<EntityListResponse<TaskDTO>>(tasks, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('TASK:SHOW')")
    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    @ResponseBody
    public ResponseEntity getTask(@PathVariable(value = "id") UUID id) {
        try {
            TaskDTO taskDTO = taskService.getTask(id);
            return new ResponseEntity<TaskDTO >(taskDTO, HttpStatus.OK);
        } catch (ObjectNotFoundException ex) {
            return new ResponseEntity<>(new ErrorResponse("db.NOT_FOUND", messageSource.getMessage("db.NOT_FOUND", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasAuthority('TASK:SAVE')")
    @RequestMapping(method = RequestMethod.POST, value = "/save")
    @ResponseBody
    public ResponseEntity saveTask(@RequestBody TaskDTO taskDTO) {
        try {
            Task task = taskService.saveTask(taskDTO);
            return new ResponseEntity<IDResponse>(new IDResponse(task.getId()), HttpStatus.OK);
        } catch (ObjectNotFoundException e) {
            return new ResponseEntity<>(new ErrorResponse("db.NOT_FOUND", messageSource.getMessage("db.NOT_FOUND", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        }

    }

    @PreAuthorize("hasAuthority('TASK:DELETE')")
    @RequestMapping(method = RequestMethod.POST, value = "/{id}/delete")
    @ResponseBody
    public ResponseEntity deleteTask(@PathVariable(value = "id") UUID id) {
        try {
            taskService.deleteTask(id);
            return new ResponseEntity<EmptyResponse>(EmptyResponse.getInstance(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ErrorResponse("db.FAILED_TO_DELETE", messageSource.getMessage("db.FAILED_TO_DELETE", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        }

    }

    @PreAuthorize("hasAuthority('TASK:RESET')")
    @RequestMapping(method = RequestMethod.POST, value = "/task-execution-log/{id}/reset")
    @ResponseBody
    public ResponseEntity resetTaskExecution(@PathVariable(value = "id") UUID id) {
        try {
            taskService.resetTaskExecution(id);
            return new ResponseEntity<EmptyResponse>(EmptyResponse.getInstance(), HttpStatus.OK);
        } catch (ObjectNotFoundException e) {
            return new ResponseEntity<>(new ErrorResponse("db.NOT_FOUND", messageSource.getMessage("db.NOT_FOUND", null, LocaleHolder.getLocale())), HttpStatus.BAD_REQUEST);
        }
    }
}
