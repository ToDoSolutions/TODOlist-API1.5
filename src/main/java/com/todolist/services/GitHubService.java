package com.todolist.services;

import com.todolist.entity.Task;
import com.todolist.entity.User;
import com.todolist.entity.github.Owner;
import com.todolist.entity.github.Repo;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.client.ClientBuilder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GitHubService {

    private static GitHubService instance = null;
    final String startUrl = "https://api.github.com";

    public static GitHubService getInstance() {
        instance = (instance == null) ? new GitHubService() : instance;
        return instance;
    }

    private static String getAdditional(Map<String, Object> additional, String key) {
        Object aux = additional.get(key);
        return aux == null ? null : aux.toString();
    }

    public Owner findOwnerByUsername(String username) {
        String url = startUrl + "/users/" + username;
        try {
            return ClientBuilder.newClient().target(url).request().get(Owner.class);
        } catch (Exception e) {
            throw new BadRequestException("Error while getting owner.");
        }
    }

    public User findUserByUsername(String username) {
        return ownerIntoUser(findOwnerByUsername(username));
    }

    public Repo findRepoByName(String username, String repo) {
        String url = startUrl + "/repos/" + username + "/" + repo;
        try {
            return ClientBuilder.newClient().target(url).request().get(Repo.class);
        } catch (Exception e) {
            throw new BadRequestException("Error while getting repo.");
        }
    }

    public Task findTaskByName(String username, String repo, String status, String finishedDate, Long priority, String difficulty) {
        return repoIntoTask(findRepoByName(username, repo), status, finishedDate, priority, difficulty);
    }

    public Repo[] findAllRepo(String username) {
        String url = findOwnerByUsername(username).getReposUrl();
        try {
            return ClientBuilder.newClient().target(url).request().get(Repo[].class);
        } catch (Exception e) {
            throw new BadRequestException("Error while getting repo.");
        }
    }

    public List<Task> findAllTasks(String username) {
        return Arrays.stream(findAllRepo(username)).map(repo -> repoIntoTask(repo, null, LocalDate.now().format(DateTimeFormatter.ISO_DATE), null, null))
                .collect(Collectors.toList());
    }

    public Task repoIntoTask(Repo repo, String status, String finishedDate, Long priority, String difficulty) {
        Task task = new Task();
        task.setTitle(repo.getName());
        task.setDescription(repo.getDescription());
        task.setStatus(status);
        task.setFinishedDate(finishedDate);
        task.setStartDate(repo.getCreatedAt().split("T")[0]);
        task.setPriority(priority);
        task.setDifficulty(difficulty);
        return task;
    }

    public User ownerIntoUser(Owner owner) {
        Map<String, Object> additional = owner.getAdditionalProperties();
        Object auxName = additional.get("name");
        List<String> fullName;
        String name = null;
        String surname = null;
        if (auxName != null) {
            fullName = Arrays.asList(additional.get("name").toString().split(" "));
            name = fullName.get(0);
            surname = fullName.size() == 1 ? null : fullName.stream().skip(1).reduce("", (ac, nx) -> ac + " " + nx);
        }
        String email = getAdditional(additional, "email");
        String bio = getAdditional(additional, "bio");
        String location = getAdditional(additional, "location");
        return User.of(name, surname, email, owner.getAvatarUrl(), bio, location);
    }
}
