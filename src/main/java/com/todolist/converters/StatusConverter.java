package com.todolist.converters;

import com.todolist.dtos.Status;
import jakarta.ws.rs.ext.ParamConverter;
import jakarta.ws.rs.ext.ParamConverterProvider;
import jakarta.ws.rs.ext.Provider;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Objects;

@Provider
public class StatusConverter implements ParamConverterProvider {

    @Override
    public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
        if (rawType.equals(Status.class)) {
            return new ParamConverter<>() {
                @Override
                public T fromString(String value) {
                    if (Objects.equals(value, "null")) return null;
                    System.out.println("fromString: " + value);
                    return rawType.cast(Status.parse(value));
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
