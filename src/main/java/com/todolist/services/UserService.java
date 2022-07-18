package com.todolist.services;

import com.todolist.dtos.ShowTask;
import com.todolist.dtos.ShowUser;
import com.todolist.entity.Task;
import com.todolist.entity.User;
import com.todolist.entity.UserTask;
import com.todolist.repositories.TaskRepository;
import com.todolist.repositories.UserRepository;
import com.todolist.repositories.UserTaskRepository;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public class UserService {

    private UserRepository userRepository;


    private TaskRepository taskRepository;


    private UserTaskRepository userTaskRepository;


    public List<ShowUser> findAllShowUsers(String order) {
        return userRepository.findAll(order).stream().map(user -> new ShowUser(user, getShowTaskFromUser(user))).collect(Collectors.toList());
    }

    public User findUserById(Long idUser) {
        return userRepository.findByIdUser(idUser);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public void deleteUser(User user) {
        userRepository.delete(user);
    }

    public List<User> findUsersWithTask(Task task) {
        return userRepository.findAll().stream().filter(user -> getTasksFromUser(user).contains(task)).toList();
    }

    public List<Task> getTasksFromUser(User user) {
        return userTaskRepository.findByIdUser(user.getIdUser()).stream()
                .map(userTask -> taskRepository.findByIdTask(userTask.getIdTask()))
                .toList();
    }

    public List<ShowTask> getShowTaskFromUser(User user) {
        return getTasksFromUser(user).stream().map(ShowTask::new).toList();
    }

    /*
    public List<Group> getGroupsFromUser(User user) {
        return groupUserRepository.findByIdUser(user.getIdUser()).stream()
                .map(groupUser -> groupRepository.findById(groupUser.getIdGroup()).orElseThrow(() -> new RuntimeException("Group not found.")))
                .toList();
    }
     */

    /*
    public List<ShowGroup> getShowGroupsFromUser(User user) {
        return getGroupsFromUser(user).stream().map(group -> new ShowGroup(group, groupService.getShowUserFromGroup(group))).collect(Collectors.toList());
    }
     */

    public void addTaskToUser(User user, Task task) {
        userTaskRepository.save(new UserTask(user.getIdUser(), task.getIdTask()));
    }

    public void removeTaskFromUser(User user, Task task) {
        List<UserTask> userTask = userTaskRepository.findByIdTaskAndIdUser(task.getIdTask(), user.getIdUser());
        if (userTask.isEmpty())
            throw new NullPointerException("The user with idUser " + user.getIdUser() + " does not have the task with idTask " + task.getIdTask() + ".|method: removeTaskFromUser");
        userTaskRepository.deleteAll(userTask);
    }

    public void removeAllTasksFromUser(User user) {
        List<UserTask> userTask = userTaskRepository.findByIdUser(user.getIdUser());
        userTaskRepository.deleteAll(userTask);
    }

    /*
    public void removeAllTasksFromUser(User user, Task task) {
        List<UserTask> userTask = userTaskRepository.findByIdTaskAndIdUser(task.getIdTask(), user.getIdUser());
        userTaskRepository.deleteAll(userTask);
    }
     */

    /*
    public void removeUserFromAllGroups(User user) {
        List<GroupUser> groupUser = groupUserRepository.findByIdUser(user.getIdUser());
        groupUserRepository.deleteAll(groupUser);
    }
    */
}
