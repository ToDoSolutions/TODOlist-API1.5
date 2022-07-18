package com.todolist.converters;

import com.todolist.filters.NumberFilter;
import jakarta.ws.rs.ext.ParamConverter;
import jakarta.ws.rs.ext.Provider;

@Provider
public class NumberFilterConverter implements ParamConverter<NumberFilter> {


    @Override
    public NumberFilter fromString(String value) {
        return NumberFilter.parse(value);
    }

    @Override
    public String toString(NumberFilter value) {
        return value.toString();
    }
}
