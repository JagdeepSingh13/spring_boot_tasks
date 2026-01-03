package com.example.tasks.services.impl;

import com.example.tasks.domain.entities.TaskList;
import com.example.tasks.repo.TaskListRepo;
import com.example.tasks.services.TaskListService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

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

    @Override
    public Optional<TaskList> getTaskList(UUID id) {
        return taskListRepo.findById(id);
    }

    @Override
    public TaskList updateTaskList(UUID taskListId, TaskList taskList) {
        if(null == taskList.getId()) {
            throw new IllegalArgumentException("task list must have id");
        }
        if(!Objects.equals(taskList.getId(), taskListId)) {
            throw new IllegalArgumentException("can't change task list id");
        }

        TaskList existingTaskList = taskListRepo.findById(taskListId)
                .orElseThrow(()-> new IllegalArgumentException("task list not found"));

        existingTaskList.setTitle(taskList.getTitle());
        existingTaskList.setDescription(taskList.getDescription());
//        existingTaskList.setTasks(taskList.getTasks());
        existingTaskList.setUpdated(LocalDateTime.now());

        return taskListRepo.save(existingTaskList);
    }

    @Override
    public void deleteTaskList(UUID taskListId) {
        taskListRepo.deleteById(taskListId);
    }


}
