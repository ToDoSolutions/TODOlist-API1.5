package com.todolist.entity;

import com.google.common.base.Objects;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class GroupUser implements Comparable<GroupUser> {

    private Long idGroupUser;
    private Long idGroup;
    private Long idUser;

    public GroupUser() {

    }

    public static GroupUser of(Long idGroup, Long idUser) {
        GroupUser groupUser = new GroupUser();
        groupUser.setIdGroup(idGroup);
        groupUser.setIdUser(idUser);
        return groupUser;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupUser groupUser = (GroupUser) o;
        return Objects.equal(idGroupUser, groupUser.idGroupUser) && Objects.equal(idGroup, groupUser.idGroup) && Objects.equal(idUser, groupUser.idUser);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(idGroupUser, idGroup, idUser);
    }

    @Override
    public int compareTo(GroupUser o) {
        return this.getIdGroupUser().compareTo(o.getIdGroupUser());
    }
}
