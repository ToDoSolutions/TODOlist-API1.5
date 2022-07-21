package com.todolist.controllers;

import com.google.common.collect.Lists;
import com.todolist.dtos.ShowGroup;
import com.todolist.dtos.ShowTask;
import com.todolist.dtos.ShowUser;
import com.todolist.entity.Group;
import com.todolist.entity.Task;
import com.todolist.entity.User;
import com.todolist.filters.DateFilter;
import com.todolist.filters.NumberFilter;
import com.todolist.services.GroupService;
import com.todolist.services.TaskService;
import com.todolist.services.UserService;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Path("/groups")
@Produces("application/json")
public class GroupResource {

    protected static GroupResource instance = null; // La instancia inicialmente no existe, se crea al ejecutar .getInstance().
    // Para poder trabajar con los datos
    final GroupService groupService;
    final UserService userService;
    final TaskService taskService;

    public GroupResource() {
        groupService = GroupService.getInstance();
        userService = UserService.getInstance();
        taskService = TaskService.getInstance();
    }

    public static GroupResource getInstance() {
        instance = (instance == null) ? new GroupResource() : instance; // Creamos una instancia si no existe.
        return instance;
    }

    @GET
    public Response getAll(@QueryParam("order") @DefaultValue("+idGroup") String order,
                           @QueryParam("limit") @DefaultValue("-1") Integer limit,
                           @QueryParam("offset") Integer offset,
                           @QueryParam("fieldsGroup") @DefaultValue(ShowGroup.ALL_ATTRIBUTES) String fieldsGroup,
                           @QueryParam("fieldsUser") @DefaultValue(ShowUser.ALL_ATTRIBUTES) String fieldsUser,
                           @QueryParam("fieldsTask") @DefaultValue(ShowTask.ALL_ATTRIBUTES) String fieldsTask,
                           @QueryParam("name") String name,
                           @QueryParam("description") String description,
                           @QueryParam("numTasks") @DefaultValue("null") NumberFilter numTasks,
                           @QueryParam("createdDate") @DefaultValue("null") DateFilter createdDate) {
        // Creamos una lista para almacenar los grupos.
        List<ShowGroup> result = Lists.newArrayList();
        // Comprobamos que el criterio de ordenación sea correcto.
        String propertyOrder = order.charAt(0) == '+' || order.charAt(0) == '-' ? order.substring(1) : order;
        if (Arrays.stream(ShowGroup.ALL_ATTRIBUTES.split(",")).noneMatch(prop -> prop.equalsIgnoreCase(propertyOrder)))
            throw new BadRequestException("The order is invalid.", Response.created(URI.create("/api/v1/tasks")).status(400).build());
        // Comprobamos que los campos dados pertenecen a un grupo.
        if (!(Arrays.stream(fieldsGroup.split(",")).allMatch(field -> ShowGroup.ALL_ATTRIBUTES.toLowerCase().contains(field.toLowerCase()))))
            throw new BadRequestException("The groups' fields are invalid.", Response.created(URI.create("/api/v1/tasks")).status(400).build());
        // Comprobamos que los campos dados pertenecen a un usuario.
        if (!(Arrays.stream(fieldsUser.split(",")).allMatch(field -> ShowUser.ALL_ATTRIBUTES.toLowerCase().contains(field.toLowerCase()))))
            throw new BadRequestException("The users' fields are invalid.", Response.created(URI.create("/api/v1/tasks")).status(400).build());
        // Comprobamos que los campos dados pertenecen a una tarea.
        if (!(Arrays.stream(fieldsTask.split(",")).allMatch(field -> ShowTask.ALL_ATTRIBUTES.toLowerCase().contains(field.toLowerCase()))))
            throw new BadRequestException("The tasks' fields are invalid.", Response.created(URI.create("/api/v1/tasks")).status(400).build());
        // Obtenemos todas las tareas.
        List<ShowGroup> groups = groupService.findAllShowGroups(order);
        // Definimos el inicio y el final.
        if (limit == -1) limit = groups.size();
        int start = offset == null || offset < 1 ? 0 : offset - 1; // Donde va a comenzar.
        int end = limit > groups.size() ? groups.size() : start + limit; // Donde va a terminar.
        // Iteramos sobre la lista con todas los grupos y realizamos el filtrado.
        for (int i = start; i < end; i++) {
            ShowGroup group = groups.get(i);
            if (group != null &&
                    (name == null || group.getName().equals(name)) &&
                    (description == null || group.getDescription().equals(description)) &&
                    (numTasks == null || numTasks.isValid(group.getNumTasks())) &&
                    (createdDate == null || createdDate.isValid(group.getCreatedDate())))
                result.add(group);
        }
        // Creamos una respuesta con la lista de grupos.
        return Response.ok(result.stream().map(g -> g.getFields(fieldsGroup, fieldsUser, fieldsTask)).collect(Collectors.toList())).build();
    }

    @GET
    @Path("/{groupId}")
    public Response getGroup(@PathParam("groupId") Long groupId,
                             @QueryParam("fieldsGroup") @DefaultValue(ShowGroup.ALL_ATTRIBUTES) String fieldsGroup,
                             @QueryParam("fieldsUser") @DefaultValue(ShowUser.ALL_ATTRIBUTES) String fieldsUser,
                             @QueryParam("fieldsTask") @DefaultValue(ShowTask.ALL_ATTRIBUTES) String fieldsTask,
                             @Context UriInfo uriInfo) {
        // Buscamos la tarea en la base de datos.
        Group group = groupService.findGroupById(groupId);
        // Comprobamos que los campos dados pertenecen a un grupo.
        if (!(Arrays.stream(fieldsGroup.split(",")).allMatch(field -> ShowGroup.ALL_ATTRIBUTES.toLowerCase().contains(field.toLowerCase()))))
            throw new BadRequestException("The groups' fields are invalid.", Response.created(URI.create("/api/v1/tasks")).status(400).build());
        // Comprobamos que los campos dados pertenecen a un usuario.
        if (!(Arrays.stream(fieldsUser.split(",")).allMatch(field -> ShowUser.ALL_ATTRIBUTES.toLowerCase().contains(field.toLowerCase()))))
            throw new BadRequestException("The users' fields are invalid.", Response.created(URI.create("/api/v1/tasks")).status(400).build());
        // Comprobamos que los campos dados pertenecen a una tarea.
        if (!(Arrays.stream(fieldsTask.split(",")).allMatch(field -> ShowTask.ALL_ATTRIBUTES.toLowerCase().contains(field.toLowerCase()))))
            throw new BadRequestException("The tasks' fields are invalid.", Response.created(URI.create("/api/v1/tasks")).status(400).build());
        // Comprobamos que existe.
        if (group == null)
            throw new NotFoundException("The group with id " + groupId + " does not exist.", Response.created(uriInfo.getRequestUri()).status(404).build());
        // Devolvemos la tarea.
        return Response.ok(new ShowGroup(group, groupService.getShowUserFromGroup(group)).getFields(fieldsGroup, fieldsUser, fieldsTask)).build();
    }

    @POST
    @Consumes("application/json")
    public Response addGroup(@Context UriInfo uriInfo, @Valid Group group) {
        // Comprobamos que se ha dado un grupo.
        if (group == null)
            throw new BadRequestException("The group is null.", Response.created(uriInfo.getRequestUri()).status(400).build());
        // Comprobamos que se han dado aquellos campos que son obligatorios.
        if (group.getName() == null || group.getName().isEmpty())
            throw new BadRequestException("The group's name is null or empty.", Response.created(uriInfo.getRequestUri()).status(400).build());
        // Si no han dado la fecha en la que se creó, damos la fecha de hoy.
        if (group.getCreatedDate() == null) group.setCreatedDate(LocalDate.now().format(DateTimeFormatter.ISO_DATE));
        // Añadimos el grupo a la base de datos.
        group = groupService.saveGroup(group);
        // Construimos la respuesta.
        return Response.created(uriInfo.getRequestUri()).entity(new ShowGroup(group, groupService.getShowUserFromGroup(group))).build();
    }

    @PUT
    @Consumes("application/json")
    public Response updateGroup(@Valid Group group) {
        // Comprobamos que se ha dado un grupo.
        if (group == null)
            throw new BadRequestException("The group is null.", Response.created(URI.create("/api/v1/groups")).status(400).build());
        //Buscamos el grupo en la base de datos.
        Group oldGroup = groupService.findGroupById(group.getIdGroup());
        // Comprobamos si el usuario existe.
        if (oldGroup == null)
            throw new NotFoundException("The group with id " + group.getIdGroup() + " does not exist.", Response.created(URI.create("/api/v1/groups")).status(404).build());
        // Actualizamos el grupo en la base de datos.
        groupService.updateGroup(group, oldGroup);
        // Devolvemos la respuesta.
        return Response.noContent().build();
    }

    @DELETE
    @Path("/{groupId}")
    public Response deleteGroup(@PathParam("groupId") Long groupId,
                                @Context UriInfo uriInfo) {
        // Buscamos el grupo en la base de datos.
        Group group = groupService.findGroupById(groupId);
        // Comprobamos si se encuentra el grupo en la base de datos.
        if (group == null)
            throw new NotFoundException("The group with id " + groupId + " does not exist.", Response.created(uriInfo.getRequestUri()).status(404).build());
        // Eliminamos el grupo de la base de datos.
        groupService.deleteGroup(group);
        // Devolvemos la respuesta.
        return Response.noContent().build();
    }

    @POST
    @Path("/{groupId}/user/{userId}")
    @Produces("application/json")
    public Response addUserToGroup(@Context UriInfo uriInfo, @PathParam("userId") Long userId, @PathParam("groupId") Long groupId) {
        // Buscamos el grupo y el usuario en la base de datos.
        Group group = groupService.findGroupById(groupId);
        User user = userService.findUserById(userId);
        // Comprobamos si se encuentran el grupo y el usuario en la base de datos.
        if (group == null)
            throw new NotFoundException("The group with id " + groupId + " does not exist.", Response.created(uriInfo.getRequestUri()).status(404).build());
        if (user == null)
            throw new NotFoundException("The user with id " + userId + " does not exist.", Response.created(uriInfo.getRequestUri()).status(404).build());
        // Añadimos el usuario al grupo.
        groupService.addUserToGroup(group, user);
        // Devolvemos la respuesta.
        return Response.created(uriInfo.getRequestUri()).entity(new ShowGroup(group, groupService.getShowUserFromGroup(group))).build();
    }

    @DELETE
    @Path("/{groupId}/user/{userId}")
    @Produces("application/json")
    public Response deleteUserToGroup(@PathParam("groupId") Long groupId, @PathParam("userId") Long userId) {
        // Buscamos el grupo y el usuario en la base de datos.
        Group group = groupService.findGroupById(groupId);
        User user = userService.findUserById(userId);
        // Comprobamos si se encuentran el grupo y el usuario en la base de datos.
        if (group == null)
            throw new NotFoundException("The group with id " + groupId + " does not exist.", Response.created(URI.create("/api/v1/groups")).status(404).build());
        if (user == null)
            throw new NotFoundException("The user with id " + userId + " does not exist.", Response.created(URI.create("/api/v1/groups")).status(404).build());
        // Y comprobamos que el usuario esté en el grupo.
        if (!groupService.hasUser(group, user))
            throw new NotFoundException("The user with id " + userId + " is not in the group with id " + groupId + ".", Response.created(URI.create("/api/v1/groups")).status(404).build());
        // Eliminamos el usuario del grupo.
        groupService.removeUserFromGroup(group, user);
        // Devolvemos la respuesta.
        return Response.noContent().build();
    }

    @POST
    @Path("/{groupId}/task/{taskId}")
    public Response addTaskToGroup(@Context UriInfo uriInfo, @PathParam("taskId") Long taskId, @PathParam("groupId") Long groupId) {
        // Buscamos el grupo y la tarea en la base de datos.
        Group group = groupService.findGroupById(groupId);
        Task task = taskService.findTaskById(taskId);
        // Comprobamos si se encuentran el grupo y la tarea en la base de datos.
        if (group == null)
            throw new NotFoundException("The group with id " + groupId + " does not exist.", Response.created(uriInfo.getRequestUri()).status(404).build());
        if (task == null)
            throw new NotFoundException("The task with id " + taskId + " does not exist.", Response.created(uriInfo.getRequestUri()).status(404).build());
        // Añadimos la tarea al grupo.
        groupService.addTaskToGroup(group, task);
        // Devolvemos la respuesta.
        return Response.created(uriInfo.getRequestUri()).entity(new ShowGroup(group, groupService.getShowUserFromGroup(group))).build();
    }

    @DELETE
    @Path("/{groupId}/task/{taskId}")
    public Response deleteTaskToGroup(@PathParam("groupId") Long groupId,
                                      @PathParam("taskId") Long taskId,
                                      @Context UriInfo uriInfo) {
        // Buscamos el grupo y la tarea en la base de datos.
        Group group = groupService.findGroupById(groupId);
        Task task = taskService.findTaskById(taskId);
        // Comprobamos si se encuentran el grupo y la tarea en la base de datos.
        if (group == null)
            throw new NotFoundException("The group with id " + groupId + " does not exist.", Response.created(URI.create("/api/v1/groups")).status(404).build());
        if (task == null)
            throw new NotFoundException("The task with id " + taskId + " does not exist.", Response.created(URI.create("/api/v1/groups")).status(404).build());
        // Y comprobamos que la tarea la tenga el grupo.
        if (!groupService.hasTask(group, task))
            throw new NotFoundException("The task with id " + taskId + " is not in the group with id " + groupId + ".", Response.created(uriInfo.getRequestUri()).status(404).build());
        // Eliminamos la tarea del grupo.
        groupService.removeTaskFromGroup(group, task);
        // Devolvemos la respuesta.
        return Response.noContent().build();
    }
}
