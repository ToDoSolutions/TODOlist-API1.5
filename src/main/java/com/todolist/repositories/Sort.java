package com.todolist.repositories;


import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.ServerErrorException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


public enum Sort {
    ASCENDING, DESCENDING;

    public static Sort parse(String order) {
        return order.charAt(0) == '+' ? Sort.ASCENDING : order.charAt(0) == '-' ? Sort.DESCENDING : Sort.ASCENDING;
    }

    public boolean isAscending() {
        return this == ASCENDING;
    }

    public boolean isDescending() {
        return this == DESCENDING;
    }

    public <T> List<T> sort(Collection<T> list, Method method) {
        return list.stream().sorted((x, y) -> {
                    try {
                        if (method.getReturnType().isInstance(int.class) || method.getReturnType().equals(Long.class))

                            return this.isAscending() ? Integer.parseInt(method.invoke(x).toString()) - Integer.parseInt(method.invoke(y).toString()) :
                                    Integer.parseInt(method.invoke(y).toString()) - Integer.parseInt(method.invoke(x).toString());
                        else if (method.getReturnType().isInstance(String.class))
                            return this.isAscending() ? ((String) method.invoke(x)).compareTo((String) method.invoke(y)) : ((String) method.invoke(y)).compareTo((String) method.invoke(x));
                        else
                            throw new ServerErrorException("Unsupported type", 500);
                    } catch (InvocationTargetException | IllegalAccessException e) {
                        throw new NotFoundException("Error while sorting");
                    }
                }
        ).collect(Collectors.toList());
    }

}

