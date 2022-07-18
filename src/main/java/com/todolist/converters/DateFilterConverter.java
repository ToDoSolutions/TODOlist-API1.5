package com.todolist.converters;


import com.todolist.filters.DateFilter;
import jakarta.ws.rs.ext.ParamConverter;

public class DateFilterConverter implements ParamConverter<DateFilter> {

    @Override
    public DateFilter fromString(String value) {
        return DateFilter.parse(value);
    }

    @Override
    public String toString(DateFilter value) {
        return value.toString();
    }
}
