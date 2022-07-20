package com.todolist.exceptions;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ExceptionResponse {
    private final String timestamp;
    private final String msg;
    private final String path;
    private final String status;

    public ExceptionResponse(String timestamp, String message, String path, String status) {
        super();
        this.timestamp = timestamp;
        this.msg = message;
        this.path = path;
        this.status = status;
    }

}
