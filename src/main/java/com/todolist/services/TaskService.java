package com.todolist.services;

import com.todolist.dtos.ShowTask;
import com.todolist.entity.Task;
import com.todolist.repositories.Sort;
import com.todolist.repositories.TaskRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TaskService {


    private static TaskService instance = null;
    private final TaskRepository taskRepository;

    private TaskService() {
        taskRepository = TaskRepository.getInstance();
    }

    public static TaskService getInstance() {
        instance = (instance == null) ? new TaskService() : instance;
        return instance;
    }

    public List<ShowTask> findAllShowTasks(String order) {
        return taskRepository.findAll(order.replace("+", "").replace("-", ""), Sort.parse(order)).stream().map(ShowTask::new).collect(Collectors.toList());
    }

    public List<Task> findAllTasks() {
        return new ArrayList<>(taskRepository.findAll());
    }

    public Task findTaskById(Long idTask) {
        return taskRepository.findByIdTask(idTask);
    }

    public Task saveTask(Task task) {
        return taskRepository.save(task);
    }

    public void deleteTask(Task task) {
        taskRepository.delete(task);
    }
}
