package com.example.tasks.services.impl;

import com.example.tasks.domain.entities.TaskList;
import com.example.tasks.repo.TaskListRepo;
import com.example.tasks.services.TaskListService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskListServiceImpl implements TaskListService {

    private final TaskListRepo taskListRepo;

    public TaskListServiceImpl(TaskListRepo taskListRepo) {
        this.taskListRepo = taskListRepo;
    }

    @Override
    public List<TaskList> listTaskLists() {
        return taskListRepo.findAll();
    }

    @Override
    public TaskList createTaskList(TaskList taskList) {
        if(null != taskList.getId()) {
            throw new IllegalArgumentException("task list already has id");
        }
        if(null == taskList.getTitle() || taskList.getTitle().isBlank()) {
            throw new IllegalArgumentException("task list must have title");
        }

        LocalDateTime now = LocalDateTime.now();
        return taskListRepo.save(new TaskList(
                null,
                taskList.getTitle(),
                taskList.getDescription(),
                null,
                now,
                now
        ));
    }

}
