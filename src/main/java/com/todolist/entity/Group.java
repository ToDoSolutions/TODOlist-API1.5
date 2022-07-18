package com.todolist.entity;


import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class Group {

    private Long generateId = 0L;
    private Long idGroup;
    @Size(max = 50, message = "The name is too long.")
    private String name;
    @Size(max = 500, message = "The description is too long.")
    private String description;
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "The createdDate is invalid.")
    private String createdDate;

    public Group() {
        this.idGroup = generateId++;
    }

    public static Group of(String name, String description, String createdDate) {
        Group group = new Group();
        group.setName(name);
        group.setDescription(description);
        group.setCreatedDate(createdDate);
        return group;
    }
}
