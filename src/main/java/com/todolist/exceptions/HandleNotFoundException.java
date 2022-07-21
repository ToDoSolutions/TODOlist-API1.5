package com.todolist.exceptions;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.net.URI;
import java.time.LocalDate;

@Provider
public class HandleNotFoundException implements ExceptionMapper<NotFoundException> {
    @Override
    public Response toResponse(NotFoundException exception) {
        String url = " --- ";

        URI location = exception.getResponse().getLocation();
        if (location != null)
            url = location.getPath();
        return Response.status(Response.Status.NOT_FOUND)
                .entity(new ExceptionResponse(
                        LocalDate.now().toString(),
                        exception.getMessage(),
                        url,
                        "Not Found"))
                .build();
    }
}
