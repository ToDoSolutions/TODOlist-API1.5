package com.todolist.services;

import com.todolist.dtos.ShowGroup;
import com.todolist.dtos.ShowUser;
import com.todolist.entity.*;
import com.todolist.repositories.*;

import java.util.List;
import java.util.stream.Collectors;


public class GroupService {


    private static GroupService instance = null;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    private final UserTaskRepository userTaskRepository;
    private final GroupUserRepository groupUserRepository;
    private final UserService userService;

    private GroupService() {
        groupRepository = GroupRepository.getInstance();
        userRepository = UserRepository.getInstance();
        userTaskRepository = UserTaskRepository.getInstance();
        groupUserRepository = GroupUserRepository.getInstance();
        userService = UserService.getInstance();
    }

    public static GroupService getInstance() {
        instance = (instance == null) ? new GroupService() : instance;
        return instance;
    }

    public List<ShowGroup> findAllShowGroups(String order) {
        return groupRepository.findAll(order.replace("+", "").replace("-", ""), Sort.parse(order)).stream().map(group -> new ShowGroup(group, getShowUserFromGroup(group))).collect(Collectors.toList());
    }

    public Group findGroupById(Long idGroup) {
        return groupRepository.findByIdGroup(idGroup);
    }

    public Group saveGroup(Group group) {
        return groupRepository.save(group);
    }

    public void updateGroup(Group group, Group oldGroup) {
        if (group.getName() != null && !group.getName().isEmpty()) oldGroup.setName(group.getName());
        if (group.getDescription() != null && !group.getDescription().isEmpty())
            oldGroup.setDescription(group.getDescription());
        if (group.getCreatedDate() != null && !group.getCreatedDate().isEmpty())
            oldGroup.setCreatedDate(group.getCreatedDate());
        groupRepository.update(oldGroup);
    }

    public void deleteGroup(Group group) {
        groupRepository.delete(group);
    }

    public List<User> getUsersFromGroup(Group group) {
        return groupUserRepository.findByIdGroup(group.getIdGroup())
                .stream().map(groupUser -> userRepository.findByIdUser(groupUser.getIdUser()))
                .collect(Collectors.toList());
    }

    public List<ShowUser> getShowUserFromGroup(Group group) {
        return getUsersFromGroup(group)
                .stream().map(user -> new ShowUser(user, userService.getShowTaskFromUser(user)))
                .collect(Collectors.toList());
    }

    public void addUserToGroup(Group group, User user) {
        groupUserRepository.save(GroupUser.of(group.getIdGroup(), user.getIdUser()));
    }

    public void removeUserFromGroup(Group group, User user) {
        List<GroupUser> groupUser = groupUserRepository.findByIdGroupAndIdUser(group.getIdGroup(), user.getIdUser());
        groupUserRepository.deleteAll(groupUser);
    }

    public void addTaskToGroup(Group group, Task task) {
        for (User user : getUsersFromGroup(group)) {
            List<UserTask> userTask = userTaskRepository.findByIdTaskAndIdUser(task.getIdTask(), user.getIdUser());
            if (userTask.isEmpty())
                userService.addTaskToUser(user, task);
        }
    }

    public void removeTaskFromGroup(Group group, Task task) {
        for (User user : getUsersFromGroup(group)) {
            List<UserTask> userTask = userTaskRepository.findByIdTaskAndIdUser(task.getIdTask(), user.getIdUser());
            if (!userTask.isEmpty())
                userService.removeTaskFromUser(user, task);
        }
    }

    public boolean hasUser(Group group, User user) {
        return getUsersFromGroup(group).contains(user);
    }

    public boolean hasTask(Group group, Task task) {
        return getUsersFromGroup(group).stream().anyMatch(user -> userService.getTasksFromUser(user).contains(task));
    }
}
