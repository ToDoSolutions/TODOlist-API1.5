package com.todolist.exceptions;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.time.ZoneId;

@Provider
public class HandleNotFoundException implements ExceptionMapper<NotFoundException> {
    @Override
    public Response toResponse(NotFoundException exception) {
        System.out.println("HandleNotFoundException");
        return Response.status(Response.Status.NOT_FOUND)
                .entity(new ExceptionResponse(
                        exception.getResponse().getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                        exception.getMessage(),
                        exception.getResponse().getLocation().getPath(),
                        "Not Found"))
                .build();
    }
}
