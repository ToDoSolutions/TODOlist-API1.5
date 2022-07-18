package com.todolist.entity;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class GroupUser {
    private Long generateId = 0L;
    private Long idGroupUser;
    private Long idGroup;
    private Long idUser;

    public GroupUser() {
        idGroupUser = generateId++;
    }
}
