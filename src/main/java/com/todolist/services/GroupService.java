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
    private UserService userService;

    private GroupService() {
        groupRepository = GroupRepository.getInstance();
        userRepository = UserRepository.getInstance();
        userTaskRepository = UserTaskRepository.getInstance();
        groupUserRepository = GroupUserRepository.getInstance();
    }

    public static GroupService getInstance() {
        instance = (instance == null) ? new GroupService() : instance;
        return instance;
    }

    public List<ShowGroup> findAllShowGroups(String order) throws NoSuchMethodException {
        return groupRepository.findAll(order.replace("+", "").replace("-", ""), Sort.parse(order)).stream().map(group -> new ShowGroup(group, getShowUserFromGroup(group))).collect(Collectors.toList());
    }

    public Group findGroupById(Long idGroup) {
        return groupRepository.findByIdGroup(idGroup);
    }

    public Group saveGroup(Group group) {
        return groupRepository.save(group);
    }

    public void deleteGroup(Group group) {
        groupRepository.delete(group);
    }

    public List<User> getUsersFromGroup(Group group) {
        return groupUserRepository.findByIdGroup(group.getIdGroup()).stream()
                .map(groupUser -> userRepository.findByIdUser(groupUser.getIdUser()))
                .collect(Collectors.toList());
    }

    public List<ShowUser> getShowUserFromGroup(Group group) {
        return getUsersFromGroup(group).stream().map(user -> new ShowUser(user, userService.getShowTaskFromUser(user))).collect(Collectors.toList());
    }

    public void addUserToGroup(Group group, User user) {
        groupUserRepository.save(GroupUser.of(group.getIdGroup(), user.getIdUser()));
    }

    public void removeUserFromGroup(Group group, User user) {
        List<GroupUser> groupUser = groupUserRepository.findByIdGroupAndIdUser(group.getIdGroup(), user.getIdUser());
        groupUserRepository.deleteAll(groupUser);
    }

    public void removeAllUsersFromGroup(Group group) {
        List<GroupUser> groupUser = groupUserRepository.findByIdGroup(group.getIdGroup());
        if (groupUser == null)
            throw new NullPointerException("The group with idGroup " + group.getIdGroup() + " does not have any user.|method: removeAllUsersFromGroup");
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

    public List<Group> findGroupsWithUser(User user) {
        return groupRepository.findAll().stream().filter(group -> getUsersFromGroup(group).contains(user)).collect(Collectors.toList());
    }

    public List<Group> findGroupsWithTask(Task task) {
        return groupRepository.findAll().stream().filter(group -> getUsersFromGroup(group).stream().anyMatch(user -> userService.getTasksFromUser(user).contains(task))).collect(Collectors.toList());
    }

    /*
    public void removeAllTasksFromGroup(Group group) {
        List<User> users = getUsersFromGroup(group);
        for (User user : users) {
            userService.removeAllTasksFromUser(user);
        }
    }

    public void addTasktoAllUser(Group group, Task task) {
        List<User> users = getUsersFromGroup(group);
        for (User user : users) {
            userService.addTaskToUser(user, task);
        }
    }
     */
}
