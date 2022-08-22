package com.todolist.services;

import com.todolist.dtos.ShowTask;
import com.todolist.dtos.ShowUser;
import com.todolist.entity.Task;
import com.todolist.entity.User;
import com.todolist.entity.UserTask;
import com.todolist.repositories.Sort;
import com.todolist.repositories.TaskRepository;
import com.todolist.repositories.UserRepository;
import com.todolist.repositories.UserTaskRepository;

import java.util.List;
import java.util.stream.Collectors;


public class UserService {

    private static UserService instance = null;

    private final UserRepository userRepository;


    private final TaskRepository taskRepository;


    private final UserTaskRepository userTaskRepository;

    public UserService() {
        userRepository = UserRepository.getInstance();
        taskRepository = TaskRepository.getInstance();
        userTaskRepository = UserTaskRepository.getInstance();
    }

    public static UserService getInstance() {
        instance = (instance == null) ? new UserService() : instance;
        return instance;
    }


    public List<ShowUser> findAllShowUsers(String order) {
        return userRepository.findAll(order.replace("+", "").replace("-", ""), Sort.parse(order)).stream().map(user -> new ShowUser(user, getShowTaskFromUser(user))).collect(Collectors.toList());
    }

    public User findUserById(Long idUser) {
        return userRepository.findByIdUser(idUser);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public void updateUser(User user, User oldUser) {
        if (user.getName() != null && !user.getName().isEmpty())
            oldUser.setName(user.getName());
        if (user.getSurname() != null && !user.getSurname().isEmpty())
            oldUser.setSurname(user.getSurname());
        if (user.getEmail() != null && !user.getEmail().isEmpty())
            oldUser.setEmail(user.getEmail());
        if (user.getAvatar() != null && !user.getAvatar().isEmpty())
            oldUser.setAvatar(user.getAvatar());
        if (user.getBio() != null && !user.getBio().isEmpty())
            oldUser.setBio(user.getBio());
        if (user.getLocation() != null && !user.getLocation().isEmpty())
            oldUser.setLocation(user.getLocation());
        userRepository.update(oldUser);
    }

    public void deleteUser(User user) {
        userRepository.delete(user);
    }

    public List<Task> getTasksFromUser(User user) {
        return userTaskRepository.findByIdUser(user.getIdUser()).stream()
                .map(userTask -> taskRepository.findByIdTask(userTask.getIdTask()))
                .collect(Collectors.toList());
    }

    public List<ShowTask> getShowTaskFromUser(User user) {
        return getTasksFromUser(user).stream().map(ShowTask::new).collect(Collectors.toList());
    }

    public void addTaskToUser(User user, Task task) {
        userTaskRepository.save(UserTask.of(user.getIdUser(), task.getIdTask()));
    }

    public void removeTaskFromUser(User user, Task task) {
        List<UserTask> userTask = userTaskRepository.findByIdTaskAndIdUser(task.getIdTask(), user.getIdUser());
        if (userTask.isEmpty())
            throw new NullPointerException("The user with idUser " + user.getIdUser() + " does not have the task with idTask " + task.getIdTask() + ".|method: removeTaskFromUser");
        userTaskRepository.deleteAll(userTask);
    }

    public boolean hasTask(User user, Task task) {
        return userTaskRepository.findByIdTaskAndIdUser(task.getIdTask(), user.getIdUser()).isEmpty();
    }
}
