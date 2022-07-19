package com.todolist.entity;


import com.google.common.base.Objects;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class Group implements Comparable<Group> {


    private Long idGroup;
    @Size(max = 50, message = "The name is too long.")
    private String name;
    @Size(max = 500, message = "The description is too long.")
    private String description;
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "The createdDate is invalid.")
    private String createdDate;

    public Group() {

    }

    public static Group of(String name, String description, String createdDate) {
        Group group = new Group();
        group.setName(name);
        group.setDescription(description);
        group.setCreatedDate(createdDate);
        return group;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Group group = (Group) o;
        return Objects.equal(idGroup, group.idGroup);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(idGroup);
    }


    @Override
    public int compareTo(Group o) {
        return this.getIdGroup().compareTo(o.getIdGroup());
    }
}
