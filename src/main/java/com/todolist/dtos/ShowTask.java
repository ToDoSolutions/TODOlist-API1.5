package com.todolist.dtos;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.todolist.entity.Task;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.time.temporal.ChronoUnit.DAYS;

@Getter
@Setter
@ToString
public class ShowTask {

    public static final String ALL_ATTRIBUTES = "idTask,title,description,status,finishedDate,startDate,annotation,priority,difficulty,duration";

    private Long idTask;
    private String title, description, annotation;
    private Status status;
    private LocalDate finishedDate, startDate;
    private Long priority;
    private Difficulty difficulty;

    public ShowTask() {
    }

    public ShowTask(Task task) {
        this.idTask = task.getIdTask();
        this.title = task.getTitle();
        this.description = task.getDescription();
        this.annotation = task.getAnnotation();
        this.status = task.getStatus() != null ? Status.parse(task.getStatus()) : null;
        this.finishedDate = LocalDate.parse(task.getFinishedDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        this.startDate = task.getStartDate() != null ? LocalDate.parse(task.getStartDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd")) : LocalDate.now();
        this.priority = task.getPriority();
        this.difficulty = task.getDifficulty() != null ? Difficulty.parse(task.getDifficulty()) : null;
    }

    public long getDuration() {
        return DAYS.between(startDate, finishedDate);
    }

    public Map<String, Object> getFields(String fields) {
        List<String> attributes = Stream.of(fields.split(",")).map(String::trim).collect(Collectors.toList());
        List<String> attributesNotNeeded = Stream.of(ALL_ATTRIBUTES.split(",")).map(String::trim).filter(attribute -> !attributes.contains(attribute)).collect(Collectors.toList());
        ObjectMapper mapper = new ObjectMapper()
                .registerModule(new ParameterNamesModule())
                .registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        Map<String, Object> map = mapper.convertValue(this, Map.class);
        for (String attribute : attributesNotNeeded) map.remove(attribute);
        return map;
    }
}
