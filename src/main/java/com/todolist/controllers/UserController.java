package com.todolist.controllers;


import com.google.common.collect.Lists;
import com.todolist.dtos.ShowTask;
import com.todolist.dtos.ShowUser;
import com.todolist.entity.Task;
import com.todolist.entity.User;
import com.todolist.filters.NumberFilter;
import com.todolist.services.TaskService;
import com.todolist.services.UserService;
import jakarta.validation.Valid;
import jakarta.validation.executable.ValidateOnExecution;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@ValidateOnExecution
@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
public class UserController {

    protected static UserController instance = null; // La instancia inicialmente no existe, se crea al ejecutar .getInstance().

    // Para poder trabajar con los datos.
    final UserService userService;
    final TaskService taskService;

    public UserController() {
        userService = UserService.getInstance();
        taskService = TaskService.getInstance();
    }

    public static UserController getInstance() {
        // Creamos una instancia si no existe.
        instance = (instance == null) ? new UserController() : instance;
        return instance;
    }

    @GET
    public Response getAll(@QueryParam("order") @DefaultValue("+idUser") String order,
                           @QueryParam("limit") @DefaultValue("-1") Integer limit,
                           @QueryParam("offset") Integer offset,
                           @QueryParam("fieldsUser") @DefaultValue(ShowUser.ALL_ATTRIBUTES) String fieldsUser,
                           @QueryParam("fieldsTask") @DefaultValue(ShowTask.ALL_ATTRIBUTES) String fieldsTask,
                           @QueryParam("name") String name,
                           @QueryParam("surname") String surname,
                           @QueryParam("email") String email,
                           @QueryParam("location") String location,
                           @QueryParam("taskCompleted") @DefaultValue("null") NumberFilter taskCompleted) {
        // Creamos una lista para almacenar las tareas.
        List<ShowUser> result = Lists.newArrayList();
        // Comprobamos que el criterio de ordenación sea correcto.
        String propertyOrder = order.charAt(0) == '+' || order.charAt(0) == '-' ? order.substring(1) : order;
        if (Arrays.stream(ShowUser.ALL_ATTRIBUTES.split(",")).noneMatch(prop -> prop.equalsIgnoreCase(propertyOrder)))
            throw new BadRequestException("The order is invalid.", Response.created(URI.create("/api/v1/users")).status(400).build());
        // Comprobamos que los campos dados pertenecen a un usuario.
        if (!(Arrays.stream(fieldsUser.split(",")).allMatch(field -> ShowUser.ALL_ATTRIBUTES.toLowerCase().contains(field.toLowerCase()))))
            throw new BadRequestException("The users' fields are invalid.", Response.created(URI.create("/api/v1/users")).status(400).build());
        // Comprobamos que los campos dados pertenecen a una tarea.
        if (!(Arrays.stream(fieldsTask.split(",")).allMatch(field -> ShowTask.ALL_ATTRIBUTES.toLowerCase().contains(field.toLowerCase()))))
            throw new BadRequestException("The tasks' fields are invalid.", Response.created(URI.create("/api/v1/users")).status(400).build());
        List<ShowUser> users = userService.findAllShowUsers(order);
        // Definimos el inicio y el extremo.
        if (limit == -1) limit = users.size();
        int start = offset == null || offset < 1 ? 0 : offset - 1; // Donde va a comenzar.
        int end = limit > users.size() ? users.size() : start + limit; // Donde va a terminar.
        // Iteramos sobre la lista con todas las tareas y realizamos el filtrado.
        for (int i = start; i < end; i++) {
            ShowUser user = users.get(i);
            if (user != null &&
                    (name == null || user.getName().equals(name)) &&
                    (surname == null || user.getSurname().equals(surname)) &&
                    (email == null || user.getEmail().equals(email)) &&
                    (location == null || user.getLocation().equals(location)) &&
                    (taskCompleted == null || taskCompleted.isValid(user.getTaskCompleted())))
                result.add(user);
        }
        // Creamos una respuesta con la lista de tareas.
        return Response.ok(result.stream().map(user -> user.getFields(fieldsUser, fieldsTask)).collect(Collectors.toList())).build();
    }


    @GET
    @Path("/{userId}")
    public Response getUser(@PathParam("userId") Long userId,
                            @QueryParam("fieldsUser") @DefaultValue(ShowUser.ALL_ATTRIBUTES) String fieldsUser,
                            @QueryParam("fieldsTask") @DefaultValue(ShowTask.ALL_ATTRIBUTES) String fieldsTask,
                            @Context UriInfo uriInfo) {
        // Buscamos el usuario en la base de datos.
        User user = userService.findUserById(userId);
        // Comprobamos que los campos dados pertenecen a un usuario.
        if (!(Arrays.stream(fieldsUser.split(",")).allMatch(field -> ShowUser.ALL_ATTRIBUTES.toLowerCase().contains(field.toLowerCase()))))
            throw new BadRequestException("The users' fields are invalid.", Response.created(URI.create("/api/v1/users")).status(400).build());
        // Comprobamos que los campos dados pertenecen a una tarea.
        if (!(Arrays.stream(fieldsTask.split(",")).allMatch(field -> ShowTask.ALL_ATTRIBUTES.toLowerCase().contains(field.toLowerCase()))))
            throw new BadRequestException("The tasks' fields are invalid.", Response.created(URI.create("/api/v1/users")).status(400).build());
        // Comprobamos que existe.
        if (user == null)
            throw new NotFoundException("The user with id " + userId + " does not exist.", Response.created(uriInfo.getRequestUri()).status(404).build());
        // Devolvemos el usuario.
        return Response.ok(new ShowUser(user, userService.getShowTaskFromUser(user)).getFields(fieldsUser, fieldsTask)).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addUser(@Context UriInfo uriInfo, @Valid User user) {
        // Comprobamos que se ha dado un usuario.
        if (user == null)
            throw new BadRequestException("The user is null.", Response.created(uriInfo.getRequestUri()).status(400).build());
        // Comprobamos que se han dado aquellos campos que son obligatorios.
        if (user.getName() == null || user.getName().isEmpty())
            throw new BadRequestException("The name is null.", Response.created(uriInfo.getRequestUri()).status(400).build());
        if (user.getSurname() == null || user.getSurname().isEmpty())
            throw new BadRequestException("The surname is null.", Response.created(uriInfo.getRequestUri()).status(400).build());
        if (user.getEmail() == null || user.getEmail().isEmpty())
            throw new BadRequestException("The email is null.", Response.created(uriInfo.getRequestUri()).status(400).build());
        if (user.getAvatar() == null || user.getAvatar().isEmpty())
            throw new BadRequestException("The avatar is null.", Response.created(uriInfo.getRequestUri()).status(400).build());
        // Añadimos el usuario en la base de datos.
        user = userService.saveUser(user);
        // Construimos la respuesta.
        return Response.created(uriInfo.getRequestUri()).entity(new ShowUser(user, userService.getShowTaskFromUser(user))).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateUser(@Valid User user) {
        // Comprobamos que se ha dado un usuario.
        if (user == null)
            throw new BadRequestException("The user is null.", Response.created(URI.create("/api/v1/users")).status(400).build());
        // Buscamos el usuario en la base de datos.
        User oldUser = userService.findUserById(user.getIdUser());
        // Comprobamos si el usuario existe.
        if (oldUser == null)
            throw new NotFoundException("The user with id " + user.getIdUser() + " does not exist.", Response.created(URI.create("/api/v1/users")).status(404).build());
        //Actualizamos el usuario en la base de datos.
        userService.updateUser(user, oldUser);
        // Devolvemos la respuesta.
        return Response.noContent().build();
    }


    @DELETE
    @Path("/{userId}")
    public Response deleteUser(@PathParam("userId") Long userId,
                               @Context UriInfo uriInfo) {
        // Buscamos el usuario en la base de datos.
        User user = userService.findUserById(userId);
        // Comprobamos si se encuentra la tarea en la base de datos.
        if (user == null)
            throw new NotFoundException("The user with id " + userId + " does not exist.", Response.created(uriInfo.getRequestUri()).status(404).build());
        // Eliminamos el usuario de la base de datos.
        userService.deleteUser(user);
        // Devolvemos la respuesta.
        return Response.noContent().build();
    }

    @POST
    @Path("/{userId}/{taskId}")
    public Response addTaskToUser(@PathParam("userId") Long userId,
                                  @PathParam("taskId") Long taskId,
                                  @Context UriInfo uriInfo) {
        // Buscamos el usuario y la tarea en la base de datos.
        User user = userService.findUserById(userId);
        Task task = taskService.findTaskById(taskId);
        // Comprobamos si se encuentran el usuario y la tarea en la base de datos.
        if (user == null)
            throw new NotFoundException("The user with id " + userId + " does not exist.", Response.created(uriInfo.getRequestUri()).status(404).build());
        if (task == null)
            throw new NotFoundException("The task with id " + taskId + " does not exist.", Response.created(uriInfo.getRequestUri()).status(404).build());
        // Añadimos la tarea al usuario.
        userService.addTaskToUser(user, task);
        // Devolvemos la respuesta.
        return Response.created(uriInfo.getRequestUri()).entity(new ShowUser(user, userService.getShowTaskFromUser(user))).build();
    }

    @DELETE
    @Path("/{userId}/{taskId}")
    public Response deleteTaskToUser(@PathParam("userId") Long userId, @PathParam("taskId") Long taskId) {
        // Buscamos el usuario y la tarea en la base de datos.
        User user = userService.findUserById(userId);
        Task task = taskService.findTaskById(taskId);
        // Comprobamos si se encuentran el usuario y la tarea en la base de datos.
        if (user == null)
            throw new NotFoundException("The user with id " + userId + " does not exist.", Response.created(URI.create("/api/v1/users")).status(404).build());
        if (task == null)
            throw new NotFoundException("The task with id " + taskId + " does not exist.", Response.created(URI.create("/api/v1/users")).status(404).build());
        // Y Comprobamos que el usuario tenga asignada esa tarea.
        if (!userService.hasTask(user, task))
            throw new NotFoundException("The user with id " + userId + " does not have the task with id " + taskId + ".", Response.created(URI.create("/api/v1/users")).status(404).build());
        // Eliminamos la tarea del usuario.
        userService.removeTaskFromUser(user, task);
        // Devolvemos la respuesta.
        return Response.noContent().build();
    }
}

