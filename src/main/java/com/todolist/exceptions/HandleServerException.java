package com.todolist.exceptions;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.rmi.ServerException;
import java.time.LocalDate;

@Provider
public class HandleServerException implements ExceptionMapper<ServerException> {
    @Override
    public Response toResponse(ServerException exception) {
        System.out.println("HandleServerException");
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ExceptionResponse(
                        LocalDate.now().toString(),
                        "Internal Server Error",
                        " --- ",
                        "Server Error"))
                .build();
    }
}
