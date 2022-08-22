package com.todolist.repositories;

import com.todolist.entity.UserTask;
import jakarta.ws.rs.BadRequestException;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;


public class UserTaskRepository {

    private static UserTaskRepository instance = null;
    private final TreeSet<UserTask> userTasks;
    private Long generatedId = 0L;

    public UserTaskRepository() {
        userTasks = new TreeSet<>();
        generateData();
    }

    public static UserTaskRepository getInstance() {
        instance = (instance == null) ? new UserTaskRepository() : instance;
        return instance;
    }

    public void generateData() {
        save(UserTask.of(0L, 0L));
        save(UserTask.of(1L, 1L));
        save(UserTask.of(2L, 2L));
        save(UserTask.of(3L, 3L));
        save(UserTask.of(4L, 4L));
        save(UserTask.of(5L, 5L));
        save(UserTask.of(5L, 6L));
    }

    public List<UserTask> findAll() {
        return new ArrayList<>(userTasks);
    }

    List<UserTask> findByIdTask(Long idTask) {
        return userTasks.stream().filter(x -> x.getIdTask().equals(idTask)).collect(Collectors.toList());
    }

    public List<UserTask> findByIdUser(Long idUser) {
        return userTasks.stream().filter(x -> x.getIdUser().equals(idUser)).collect(Collectors.toList());
    }

    public List<UserTask> findByIdTaskAndIdUser(Long idTask, Long idUser) {
        return userTasks.stream().filter(x -> x.getIdTask().equals(idTask) && x.getIdUser().equals(idUser)).collect(Collectors.toList());
    }

    public void save(UserTask userTask) {
        userTask.setIdUserTask(generatedId++);
        if (userTasks.add(userTask)) {
        }
        else throw new BadRequestException("UserTask already exists");
    }

    public void deleteAll(List<UserTask> userTask) {
        if (userTasks.removeAll(userTask)) {
        }
        else throw new BadRequestException("UserTask not found");
    }
}
