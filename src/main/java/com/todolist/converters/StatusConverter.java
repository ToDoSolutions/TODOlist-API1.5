package com.todolist.converters;

import com.todolist.dtos.Status;
import jakarta.ws.rs.ext.ParamConverter;
import jakarta.ws.rs.ext.Provider;

@Provider
public class StatusConverter implements ParamConverter<Status> {

    @Override
    public Status fromString(String value) {
        return Status.valueOf(value);
    }

    @Override
    public String toString(Status value) {
        return value.toString();
    }
}
