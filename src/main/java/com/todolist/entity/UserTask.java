package com.todolist.entity;


import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class UserTask {

    private Long generateId = 0L;
    private Long idUserTask;
    private Long idTask;
    private Long idUser;

    public UserTask() {
    }

    public UserTask(long idTask, long idUser) {
        this.idUserTask = generateId++;
        this.idTask = idTask;
        this.idUser = idUser;
    }
}
