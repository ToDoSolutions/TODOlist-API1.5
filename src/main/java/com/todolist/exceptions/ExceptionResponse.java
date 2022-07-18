package com.todolist.exceptions;

import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@ToString
public class ExceptionResponse {
    private final LocalDate timestamp;
    private final String msg;
    private final String path;
    private final String status;

    public ExceptionResponse(LocalDate timestamp, String message, String path, String status) {
        super();
        this.timestamp = timestamp;
        this.msg = message;
        this.path = path;
        this.status = status;
    }

}
