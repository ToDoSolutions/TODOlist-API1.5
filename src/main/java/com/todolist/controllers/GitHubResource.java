package com.todolist.controllers;


import com.todolist.dtos.ShowTask;
import com.todolist.dtos.ShowUser;
import com.todolist.entity.User;
import com.todolist.services.GitHubService;
import com.todolist.services.TaskService;
import com.todolist.services.UserService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.net.URI;
import java.util.Arrays;

@Path("/github")
@Produces("application/json")
public class GitHubResource {

    protected static GitHubResource instance = null; // La instancia inicialmente no existe, se crea al ejecutar .getInstance().
    // Para poder trabajar con los datos
    final GitHubService gitHubService;
    final TaskService taskService;
    final UserService userService;

    private GitHubResource() {
        gitHubService = GitHubService.getInstance();
        taskService = TaskService.getInstance();
        userService = UserService.getInstance();
    }

    public static GitHubResource getInstance() {
        // Creamos una instancia si no existe.
        instance = (instance == null) ? new GitHubResource() : instance;
        return instance;
    }

    @GET
    @Path("repos/{username}")
    public Response getAllTask(@PathParam("username") String username) {
        return Response.ok(gitHubService.findAllTasks(username).stream().map(ShowTask::new)).build();
    }


    @GET
    @Path("/{username}/{repo}")
    public Response getTask(@PathParam("username") String username, @PathParam("repo") String repo,
                            @QueryParam("status") String status, @QueryParam("finishedDate") String finishedDate, @QueryParam("priority") Long priority,
                            @QueryParam("difficulty") String difficulty) {
        return Response.ok(gitHubService.findTaskByName(username, repo, status, finishedDate, priority, difficulty)).build();


    }

    @POST
    @Path("/{account}/{repo}")
    public Response addTask(@Context UriInfo uriInfo, @PathParam("account") String account, @PathParam("repo") String repo,
                            @QueryParam("status") String status, @QueryParam("finishedDate") String finishedDate, @QueryParam("priority") Long priority,
                            @QueryParam("difficulty") String difficulty) {
        return Response.created(uriInfo.getRequestUri())
                .entity(new ShowTask(taskService.saveTask(gitHubService.findTaskByName(account, repo, status, finishedDate, priority, difficulty))))
                .build();
    }

    @GET
    @Path("/{username}")
    @Produces("application/json")
    public Response getUser(@PathParam("username") String username) {
        User user = gitHubService.findUserByUsername(username);
        return Response.ok(new ShowUser(user, userService.getShowTaskFromUser(user))).build();
    }

    @POST
    @Path("/{username}")
    @Produces("application/json")
    public Response addUser(@Context UriInfo uriInfo, @PathParam("username") String username) {
        User user = userService.saveUser(gitHubService.findUserByUsername(username));
        return Response.created(uriInfo.getRequestUri())
                .entity(new ShowUser(user, userService.getShowTaskFromUser(user))).build();
    }
}
