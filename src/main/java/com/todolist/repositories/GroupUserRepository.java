package com.todolist.repositories;

import com.todolist.entity.GroupUser;
import jakarta.ws.rs.BadRequestException;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;


public class GroupUserRepository {

    private static GroupUserRepository instance = null;

    private final TreeSet<GroupUser> groupUsers;

    private Long generatedId = 0L;

    public GroupUserRepository() {
        groupUsers = new TreeSet<>();
        generateData();
    }

    public static GroupUserRepository getInstance() {
        instance = (instance == null) ? new GroupUserRepository() : instance;
        return instance;
    }

    private void generateData() {
        save(GroupUser.of(0L, 0L));
        save(GroupUser.of(0L, 1L));
        save(GroupUser.of(1L, 2L));
        save(GroupUser.of(1L, 3L));
        save(GroupUser.of(2L, 4L));
        save(GroupUser.of(2L, 5L));
    }

    public List<GroupUser> findAll() {
        return new ArrayList<>(groupUsers);
    }

    public List<GroupUser> findByIdGroup(Long idGroup) {
        return groupUsers.stream().filter(x -> x.getIdGroup().equals(idGroup)).collect(Collectors.toList());
    }

    public List<GroupUser> findByIdUser(Long idUser) {
        return groupUsers.stream().filter(x -> x.getIdUser().equals(idUser)).collect(Collectors.toList());
    }

    public List<GroupUser> findByIdGroupAndIdUser(Long idGroup, Long idUser) {

        return groupUsers.stream().filter(x -> x.getIdGroup().equals(idGroup) && x.getIdUser().equals(idUser)).collect(Collectors.toList());
    }

    public GroupUser save(GroupUser groupUser) {
        groupUser.setIdGroupUser(generatedId++);
        if (groupUsers.add(groupUser)) return groupUser;
        else throw new BadRequestException("GroupUser already exists");
    }

    public List<GroupUser> deleteAll(List<GroupUser> groupUsers) {
        if (groupUsers.removeAll(groupUsers)) return groupUsers;
        else throw new BadRequestException("GroupUser not found");
    }
}
