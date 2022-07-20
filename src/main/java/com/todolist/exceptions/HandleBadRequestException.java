package com.todolist.exceptions;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.net.URI;
import java.time.LocalDate;

@Provider
public class HandleBadRequestException implements ExceptionMapper<BadRequestException> {
    @Override
    public Response toResponse(BadRequestException exception) {
        System.out.println("HandleBadRequestException");
        String url = " --- ";
        URI location = exception.getResponse().getLocation();
        if (location != null)
            url = location.getPath();
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ExceptionResponse(
                        LocalDate.now().toString(),
                        exception.getMessage(),
                        url,
                        "Bad Request"))
                .build();
    }
}
