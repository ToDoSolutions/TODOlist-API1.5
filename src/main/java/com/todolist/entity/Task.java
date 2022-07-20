package com.todolist.entity;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(of = {"idTask"})
@NoArgsConstructor
public class Task implements Comparable<Task> {
    private Long idTask;
    @Size(max = 50, message = "The title is too long.")
    private String title;
    @Size(max = 200, message = "The description is too long.")
    private String description;
    @Size(max = 50, message = "The annotation is too long.")
    private String annotation;
    @Pattern(regexp = "[Dd][Rr][Aa][Ff][Tt]|[Ii][Nn][_ ][Pp][Rr][Oo][Gg][Rr][Ee][Ss][Ss]|[Dd][Oo][Nn][Ee]|[Ii][Nn][_ ][Rr][Ee][Vv][Ii][Ss][Ii][Oo][Nn]|[Cc][Aa][Nn][Cc][Ee][Ll][Ll][Ee][Dd]", message = "The status is invalid.")
    private String status;
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "The finishedDate must be in format yyyy-MM-dd.")
    private String finishedDate;
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "The startDate must be in format yyyy-MM-dd.")
    private String startDate;
    @Max(value = 5, message = "The priority must be between 0 and 5.")
    @Min(value = 0, message = "The priority must be between 0 and 5.")
    private Long priority;
    @Pattern(regexp = "[Ss][Ll][Ee][Ee][Pp]|[Ee][Aa][Ss][Yy]|[Mm][Ee][Dd][Ii][Uu][Mm]|[Hh][Aa][Rr][Dd]|[Hh][Aa][Rr][Dd][Cc][Oo][Rr][Ee]|[Ii][_ ][Ww][Aa][Nn][Tt][_ ][Tt][Oo][_ ][Dd][Ii][Ee]", message = "The difficulty is invalid.")
    private String difficulty;

    public static Task of(String title, String description, String annotation, String status, String finishedDate, String startDate, Long priority, String difficulty) {
        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setAnnotation(annotation);
        task.setStatus(status);
        task.setFinishedDate(finishedDate);
        task.setStartDate(startDate);
        task.setPriority(priority);
        task.setDifficulty(difficulty);
        return task;
    }

    @Override
    public int compareTo(Task o) {
        return this.getIdTask().compareTo(o.getIdTask());
    }
}
