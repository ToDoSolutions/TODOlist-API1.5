package com.todolist.entity;


import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@EqualsAndHashCode(of = {"idGroup"})
@NoArgsConstructor
public class Group implements Comparable<Group> {


    private Long idGroup;
    @Size(max = 50, message = "The name is too long.")
    private String name;
    @Size(max = 500, message = "The description is too long.")
    private String description;
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "The createdDate is invalid.")
    private String createdDate;

    public static Group of(String name, String description, String createdDate) {
        Group group = new Group();
        group.setName(name);
        group.setDescription(description);
        group.setCreatedDate(createdDate);
        return group;
    }

    @Override
    public int compareTo(Group o) {
        return this.getIdGroup().compareTo(o.getIdGroup());
    }
}
