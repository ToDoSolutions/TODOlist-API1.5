package com.todolist.services;

import com.todolist.dtos.ShowTask;
import com.todolist.entity.Task;
import com.todolist.repositories.TaskRepository;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public class TaskService {


    private TaskRepository taskRepository;

    public List<ShowTask> findAllShowTasks(String order) {
        return taskRepository.findAll(order).stream().map(ShowTask::new).collect(Collectors.toList());
    }

    public List<Task> findAllTasks() {
        return taskRepository.findAll().stream().collect(Collectors.toList());
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
