package com.gracelogic.platform.task.service;

import org.springframework.beans.factory.annotation.Autowired;

public class TaskJob {
    @Autowired
    private TaskService taskService;

    public void run() {
        taskService.scheduleCronTasks();

        taskService.startNextTask();
    }
}
