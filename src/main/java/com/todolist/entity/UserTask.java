package com.todolist.entity;


import com.google.common.base.Objects;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class UserTask implements Comparable<UserTask> {
    private Long idUserTask;
    private Long idTask;
    private Long idUser;

    public UserTask() {
    }

    public static UserTask of(Long idUser, Long idTask) {
        UserTask userTask = new UserTask();
        userTask.setIdTask(idTask);
        userTask.setIdUser(idUser);
        return userTask;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserTask userTask = (UserTask) o;
        return Objects.equal(idUserTask, userTask.idUserTask) && Objects.equal(idTask, userTask.idTask) && Objects.equal(idUser, userTask.idUser);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(idUserTask, idTask, idUser);
    }

    @Override
    public int compareTo(UserTask o) {
        return this.getIdUserTask().compareTo(o.getIdUserTask());
    }
}
