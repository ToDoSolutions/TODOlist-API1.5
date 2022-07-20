package com.todolist.dtos;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.todolist.entity.Group;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@Setter
@ToString
public class ShowGroup {

    public static final String ALL_ATTRIBUTES = "idGroup,name,description,createdDate,users,numTasks";
    private Long idGroup;
    private String name;
    private String description;
    private LocalDate createdDate;

    private List<ShowUser> users;

    public ShowGroup(Group group, List<ShowUser> users) {
        this.idGroup = group.getIdGroup();
        this.name = group.getName();
        this.description = group.getDescription();
        this.createdDate = group.getCreatedDate() != null ? LocalDate.parse(group.getCreatedDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd")) : LocalDate.now();
        this.users = users;
    }

    public Long getNumTasks() {
        return (long) users.stream()
                .flatMap(user -> user.getTasks().stream().map(ShowTask::getIdTask))
                .collect(Collectors.toSet()).size();
    }

    public Map<String, Object> getFields(String fieldsGroup, String fieldsUser, String fieldsTask) {
        List<String> attributes = Stream.of(fieldsGroup.split(",")).map(String::trim).collect(Collectors.toList());
        List<String> attributesNotNeeded = Stream.of(ALL_ATTRIBUTES.split(",")).map(String::trim).filter(attribute -> !attributes.contains(attribute)).collect(Collectors.toList());
        ObjectMapper mapper = new ObjectMapper()
                .registerModule(new ParameterNamesModule())
                .registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        Map<String, Object> map = mapper.convertValue(this, Map.class);
        for (String attribute : attributesNotNeeded) map.remove(attribute);
        if (attributes.contains("users"))
            map.put("users", getUsers().stream().map(task -> task.getFields(fieldsUser, fieldsTask)).collect(Collectors.toList()));
        return map;
    }
}
