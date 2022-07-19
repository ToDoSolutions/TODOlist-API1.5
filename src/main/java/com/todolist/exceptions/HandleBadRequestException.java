package com.todolist.exceptions;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.time.ZoneId;

@Provider
public class HandleBadRequestException implements ExceptionMapper<BadRequestException> {
    @Override
    public Response toResponse(BadRequestException exception) {
        System.out.println("HandleBadRequestException");
        return Response.status(Response.Status.NOT_FOUND)
                .entity(new ExceptionResponse(
                        exception.getResponse().getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                        exception.getMessage(),
                        exception.getResponse().getLocation().getPath(),
                        "Bad Request"))
                .build();
    }
}
