package com.example.tasks.services.impl;

import com.example.tasks.domain.entities.Task;
import com.example.tasks.domain.entities.TaskList;
import com.example.tasks.domain.entities.TaskPriority;
import com.example.tasks.domain.entities.TaskStatus;
import com.example.tasks.repo.TaskListRepo;
import com.example.tasks.repo.TaskRepo;
import com.example.tasks.services.TaskService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;


@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepo taskRepo;
    private final TaskListRepo taskListRepo;

    public TaskServiceImpl(TaskRepo taskRepo, TaskListRepo taskListRepo) {
        this.taskRepo = taskRepo;
        this.taskListRepo = taskListRepo;
    }

    @Override
    public List<Task> listTasks(UUID taskListId) {
        return taskRepo.findByTaskListId(taskListId);
    }

//    when we have multiple DB calls
    @Transactional
    @Override
    public Task createTask(UUID taskListId, Task task) {
        if(null != task.getId()) {
            throw new IllegalArgumentException("task already has an id");
        }
        if(null == task.getTitle() || task.getTitle().isBlank()) {
            throw new IllegalArgumentException("task does not has a title");
        }

        TaskPriority taskPriority =  Optional.ofNullable(task.getPriority())
                .orElse(TaskPriority.MEDIUM);

        TaskStatus taskStatus = TaskStatus.OPEN;

        TaskList taskList = taskListRepo.findById(taskListId)
                .orElseThrow(()-> new IllegalArgumentException("invalid task List id"));

        LocalDateTime now = LocalDateTime.now();
        Task taskToSave = new Task(
                null,
                task.getTitle(),
                task.getDescription(),
                task.getDueDate(),
                taskStatus,
                taskPriority,
                taskList,
                now,
                now
        );

        return taskRepo.save(taskToSave);
    }

    @Override
    public Optional<Task> getTask(UUID taskListId, UUID taskId) {
        return taskRepo.findByTaskListIdAndId(taskListId, taskId);
    }

    @Transactional
    @Override
    public Task updateTask(UUID taskListId, UUID taskId, Task task) {
        if(null == task.getId()) {
            throw new IllegalArgumentException("task must have an id");
        }
        if(!Objects.equals(taskId, task.getId())) {
            throw new IllegalArgumentException("task id's do not match");
        }
        if(null == task.getPriority()) {
            throw new IllegalArgumentException("task must have a valid priority");
        }
        if(null == task.getStatus()) {
            throw new IllegalArgumentException("task must have a valid status");
        }

        Task existingTask = taskRepo.findByTaskListIdAndId(taskListId, taskId)
                .orElseThrow(() -> new IllegalArgumentException("task not found"));

        existingTask.setTitle(task.getTitle());
        existingTask.setDescription(task.getDescription());
        existingTask.setDueDate(task.getDueDate());
        existingTask.setPriority(task.getPriority());
        existingTask.setStatus(task.getStatus());
        existingTask.setUpdated(LocalDateTime.now());

        return taskRepo.save(existingTask);
    }

    @Transactional
    @Override
    public void deleteTask(UUID taskListId, UUID taskId) {
        taskRepo.deleteByTaskListIdAndId(taskListId, taskId);
    }

}
