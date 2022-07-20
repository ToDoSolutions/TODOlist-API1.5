package com.todolist.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@EqualsAndHashCode(of = {"idGroupUser", "idGroup", "idUser"})
@NoArgsConstructor
public class GroupUser implements Comparable<GroupUser> {

    private Long idGroupUser;
    private Long idGroup;
    private Long idUser;

    public static GroupUser of(Long idGroup, Long idUser) {
        GroupUser groupUser = new GroupUser();
        groupUser.setIdGroup(idGroup);
        groupUser.setIdUser(idUser);
        return groupUser;
    }

    @Override
    public int compareTo(GroupUser o) {
        return this.getIdGroupUser().compareTo(o.getIdGroupUser());
    }
}
