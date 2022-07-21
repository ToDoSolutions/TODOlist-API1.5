package com.todolist.repositories;

import com.todolist.entity.Group;
import jakarta.ws.rs.BadRequestException;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class GroupRepository {

    private static GroupRepository instance = null;
    private final GroupUserRepository groupUserRepository;
    private TreeSet<Group> groups;

    private Long generatedId = 0L;

    public GroupRepository() {
        groups = new TreeSet<>();
        generateData();
        groupUserRepository = GroupUserRepository.getInstance();
    }

    public static GroupRepository getInstance() {
        instance = (instance == null) ? new GroupRepository() : instance;
        return instance;
    }

    public void generateData() {
        save(Group.of("Pepe", "Solo quieren ver el mundo arder", "2006-10-12"));
        save(Group.of("Otakus", "Dicen que su factura del agua es negativa", "2022-05-06"));
        save(Group.of("AISS enjoyers", "Se dice que son seres que existen desde el inicio de los multiversos", "2000-03-09"));
    }

    public void deleteData() {
        generatedId = 0L;
        groups.clear();
    }

    public List<Group> findAll() {
        return new ArrayList<>(groups);
    }

    public List<Group> findAll(String order, Sort sort) {
        String nameMethod = "get" + order.substring(0, 1).toUpperCase() + order.substring(1);
        Method method;
        try {
            method = Group.class.getMethod(nameMethod);
        } catch (NoSuchMethodException e) {
            throw new BadRequestException("Order not found");
        }

        return sort.sort(groups, method);
    }


    public Group findByIdGroup(Long idGroup) {
        return groups.stream().filter(x -> x.getIdGroup().equals(idGroup)).findFirst().orElse(null);
    }

    public Group save(Group group) {
        group.setIdGroup(generatedId++);
        if (groups.add(group)) return group;
        else throw new BadRequestException("Group already exists");
    }

    public Group update(Group newGroup) {
        groups = groups.stream().map(group -> {
            if (newGroup.getIdGroup().equals(group.getIdGroup())) return newGroup;
            else return group;
        }).collect(Collectors.toCollection(TreeSet::new));
        return newGroup;
    }

    public Group delete(Group group) {
        if (!groups.remove(group)) throw new BadRequestException("Group not found");
        groupUserRepository.deleteAll(groupUserRepository.findByIdGroup(group.getIdGroup()));
        return group;
    }
}
