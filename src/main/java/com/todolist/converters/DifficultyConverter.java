package com.todolist.converters;

import com.todolist.dtos.Difficulty;
import jakarta.ws.rs.ext.ParamConverter;
import jakarta.ws.rs.ext.Provider;


@Provider
public class DifficultyConverter implements ParamConverter<Difficulty> {


    @Override
    public Difficulty fromString(String value) {
        return Difficulty.parse(value);
    }

    @Override
    public String toString(Difficulty value) {
        return value.toString();
    }
}
