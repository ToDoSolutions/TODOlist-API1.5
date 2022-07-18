package com.todolist.dtos;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.todolist.entity.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Getter
@Setter
@ToString
public class ShowUser {
    public static final String ALL_ATTRIBUTES = "idUser,name,surname,email,avatar,bio,location,taskCompleted,tasks";
    private Long idUser;
    private String name;
    private String surname;
    private String username;
    private String email;
    private String avatar;
    private String bio;
    private String location;
    private List<ShowTask> tasks;

    public ShowUser() {
    }


    public ShowUser(User user, List<ShowTask> tasks) {
        this.idUser = user.getIdUser();
        this.name = user.getName();
        this.surname = user.getSurname();
        this.email = user.getEmail();
        this.avatar = user.getAvatar();
        this.bio = user.getBio();
        this.location = user.getLocation();
        this.username = user.getUsername();
        this.tasks = tasks;
    }

    public Long getTaskCompleted() {
        return getTasks().stream().filter(task -> task.getStatus().equals(Status.DONE)).count();
    }

    public Map<String, Object> getFields(String fieldsUser, String fieldsTask) {
        List<String> attributes = Stream.of(fieldsUser.split(",")).map(String::trim).toList();
        List<String> attributesNotNeeded = Stream.of(ALL_ATTRIBUTES.split(",")).map(String::trim).filter(attribute -> !attributes.contains(attribute)).toList();
        ObjectMapper mapper = new ObjectMapper()
                .registerModule(new ParameterNamesModule())
                .registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        Map<String, Object> map = mapper.convertValue(this, Map.class);
        for (String attribute : attributesNotNeeded) map.remove(attribute);
        if (attributes.contains("tasks"))
            map.put("tasks", getTasks().stream().map(task -> task.getFields(fieldsTask)).toList());
        return map;
    }
}
