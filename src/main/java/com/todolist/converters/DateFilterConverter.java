package com.todolist.converters;


import com.todolist.filters.DateFilter;
import jakarta.ws.rs.ext.ParamConverter;
import jakarta.ws.rs.ext.ParamConverterProvider;
import jakarta.ws.rs.ext.Provider;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Objects;

@Provider
public class DateFilterConverter implements ParamConverterProvider {

    @Override
    public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
        if (rawType.equals(DateFilter.class)) {
            return new ParamConverter<>() {
                @Override
                public T fromString(String value) {
                    if (Objects.equals(value, "null")) return null;
                    return rawType.cast(DateFilter.parse(value));
                }

                @Override
                public String toString(T value) {
                    return value.toString();
                }
            };
        }
        return null;
    }
}
