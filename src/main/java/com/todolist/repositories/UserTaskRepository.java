package com.todolist.repositories;

import com.todolist.entity.UserTask;

import java.util.List;

public class UserTaskRepository {

    public List<UserTask> findAll() {
        return null;
    }

    List<UserTask> findByIdTask(Long idTask) {
        return null;
    }

    public List<UserTask> findByIdUser(Long idUser) {
        return null;
    }

    public List<UserTask> findByIdTaskAndIdUser(Long idTask, Long idUser) {
        return null;
    }

    public void save(UserTask userTask) {
    }

    public void deleteAll(List<UserTask> userTask) {
    }
}
