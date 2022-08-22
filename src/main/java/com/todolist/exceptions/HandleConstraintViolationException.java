package com.todolist.exceptions;

import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.time.LocalDate;

@Provider
public class HandleConstraintViolationException implements ExceptionMapper<ConstraintViolationException> {

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ExceptionResponse(
                        LocalDate.now().toString(),
                        exception.getMessage(),
                        "When creating or updating an object.",
                        "Bad Request"))
                .build();
    }
}
