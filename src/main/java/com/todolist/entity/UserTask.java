package com.todolist.entity;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@EqualsAndHashCode(of = {"idUserTask", "idUser", "idTask"})
@NoArgsConstructor
public class UserTask implements Comparable<UserTask> {
    private Long idUserTask;
    private Long idTask;
    private Long idUser;

    public static UserTask of(Long idUser, Long idTask) {
        UserTask userTask = new UserTask();
        userTask.setIdTask(idTask);
        userTask.setIdUser(idUser);
        return userTask;
    }

    @Override
    public int compareTo(UserTask o) {
        return this.getIdUserTask().compareTo(o.getIdUserTask());
    }
}
