package com.todolist.controllers;


import com.google.common.collect.Lists;
import com.todolist.dtos.Difficulty;
import com.todolist.dtos.ShowTask;
import com.todolist.dtos.Status;
import com.todolist.entity.Task;
import com.todolist.filters.DateFilter;
import com.todolist.filters.NumberFilter;
import com.todolist.services.TaskService;
import jakarta.validation.Valid;
import jakarta.validation.executable.ValidateOnExecution;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@ValidateOnExecution
@Path("/tasks")
@Produces(MediaType.APPLICATION_JSON)
public class TaskResource {

    protected static TaskResource instance = null;
    final TaskService taskService; // Para poder trabajar con los datos

    public TaskResource() {
        taskService = TaskService.getInstance();
    }

    public static TaskResource getInstance() {
        instance = (instance == null) ? new TaskResource() : instance;
        return instance;
    }


    @GET
    public Response getAllTasks(@QueryParam("order") @DefaultValue("+idTask") String order,
                                @QueryParam("limit") @DefaultValue("-1") Integer limit,
                                @QueryParam("offset") Integer offset,
                                @QueryParam("fields") @DefaultValue(ShowTask.ALL_ATTRIBUTES) String fields,
                                @QueryParam("title") String title,
                                @QueryParam("description") String description,
                                @QueryParam("annotation") String annotation,
                                @QueryParam("status") @DefaultValue("null") Status status,
                                @QueryParam("startDate") @DefaultValue("null") DateFilter startDate,
                                @QueryParam("finishedDate") @DefaultValue("null") DateFilter finishedDate,
                                @QueryParam("priority") @DefaultValue("null") NumberFilter priority,
                                @QueryParam("difficulty") @DefaultValue("null") Difficulty difficulty,
                                @QueryParam("duration") @DefaultValue("null") NumberFilter duration) {
        // Creamos una lista para almacenar las tareas.
        List<ShowTask> result = Lists.newArrayList();
        // Comprobamos que el criterio de ordenación sea correcto.
        String propertyOrder = order.charAt(0) == '+' || order.charAt(0) == '-' ? order.substring(1) : order;
        if (Arrays.stream(ShowTask.ALL_ATTRIBUTES.split(",")).noneMatch(prop -> prop.equalsIgnoreCase(propertyOrder)))
            throw new BadRequestException("The order is invalid.", Response.created(URI.create("/api/v1/tasks")).status(400).build());
        // Comprobamos que los campos dados pertenecen a una tarea.
        if (!(Arrays.stream(fields.split(",")).allMatch(field -> ShowTask.ALL_ATTRIBUTES.toLowerCase().contains(field.toLowerCase()))))
            throw new BadRequestException("The fields are invalid.", Response.created(URI.create("/api/v1/tasks")).status(400).build());
        // Obtenemos todas las tareas.
        List<ShowTask> tasks = taskService.findAllShowTasks(order);
        // Definimos el inicio y el final.
        if (limit == -1) limit = tasks.size();
        int start = offset == null || offset < 1 ? 0 : offset - 1; // Donde va a comenzar.
        int end = limit > tasks.size() ? tasks.size() : start + limit; // Donde va a terminar.
        // Iteramos sobre la lista con todas las tareas y realizamos el filtrado.
        for (int i = start; i < end; i++) {
            ShowTask task = tasks.get(i);
            if (task != null &&
                    (title == null || task.getTitle().contains(title)) &&
                    (description == null || task.getDescription().contains(description)) &&
                    (annotation == null || task.getAnnotation().contains(annotation)) &&
                    (status == null || task.getStatus().equals(status)) &&
                    (startDate == null || startDate.isValid(task.getStartDate())) &&
                    (finishedDate == null || finishedDate.isValid(task.getFinishedDate())) &&
                    (priority == null || priority.isValid(task.getPriority())) &&
                    (difficulty == null || task.getDifficulty().equals(difficulty)) &&
                    (duration == null || duration.isValid(task.getDuration())))
                result.add(task);
        }
        // Creamos una respuesta con la lista de tareas.
        return Response.ok(result.stream().map(task -> task.getFields(fields)).collect(Collectors.toList())).build();
    }


    @GET
    @Path("/{taskId}")
    public Response getTask(@PathParam("taskId") Long taskId,
                            @QueryParam("fields") @DefaultValue(ShowTask.ALL_ATTRIBUTES) String fields,
                            @Context UriInfo uriInfo) {
        // Buscamos la tarea en la base de datos.
        Task task = taskService.findTaskById(taskId);
        // Comprobamos que existe.
        if (task == null)
            throw new NotFoundException("Task not found.", Response.created(uriInfo.getRequestUri()).status(404).build()); // Comprobamos si se encuentra el objeto en la base de datos chapucera.
        // Devolvemos la tarea.
        return Response.ok(new ShowTask(task).getFields(fields)).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addTask(@Context UriInfo uriInfo, @Valid Task task) {
        // Comprobamos que se ha dado una tarea.
        if (task == null)
            throw new BadRequestException("Task is null.", Response.created(URI.create("/api/v1/tasks")).status(400).build());
        // Comprobamos que se han dado aquellos campos que son obligatorios.
        if (task.getTitle() == null || task.getTitle().isEmpty())
            throw new BadRequestException("Task title is null or empty.", Response.created(URI.create("/api/v1/tasks")).status(400).build());
        if (task.getDescription() == null || task.getDescription().isEmpty())
            throw new BadRequestException("Task description is null or empty.", Response.created(URI.create("/api/v1/tasks")).status(400).build());
        if (task.getFinishedDate() == null || task.getFinishedDate().isEmpty())
            throw new BadRequestException("Task finish date is null.", Response.created(URI.create("/api/v1/tasks")).status(400).build());
        // Si no han dado la fecha de comienzo, damos la fecha de hoy.
        if (task.getStartDate() == null) task.setStartDate(LocalDate.now().format(DateTimeFormatter.ISO_DATE));
        // Creamos un objeto ShowTask para poder mostrar la tarea.
        ShowTask showTask = new ShowTask(task);
        // Comprobamos que la fecha de inicio y la de hoy no sea posterior a la de finalización.
        if (!showTask.getStartDate().isBefore(showTask.getFinishedDate()))
            throw new BadRequestException("Task start date is after task finished date.", Response.created(URI.create("/api/v1/tasks")).status(400).build());
        if (!showTask.getFinishedDate().isBefore(LocalDate.now()))
            throw new BadRequestException("Task finished date is before today.", Response.created(URI.create("/api/v1/tasks")).status(4040).build());
        // Añadimos la tarea a la base de datos.
        task = taskService.saveTask(task);
        showTask = new ShowTask(task);
        // Construimos la respuesta.
        return Response.created(uriInfo.getRequestUri()).entity(showTask).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateTask(@Valid Task task) {
        // Comprobamos que se ha dado una tarea.
        if (task.getIdTask() == null)
            throw new BadRequestException("Task id is null.", Response.created(URI.create("/api/v1/tasks")).status(400).build());
        // Buscamos la tarea en la base de datos.
        Task oldTask = taskService.findTaskById(task.getIdTask());
        // Comprobamos si la tarea existe.
        if (oldTask == null)
            throw new NotFoundException("The task with idTask " + task.getIdTask() + " does not exist.", Response.created(URI.create("/api/v1/tasks")).status(404).build());
        // Actualizamos la tarea.
        if (task.getTitle() != null && !Objects.equals(task.getTitle(), ""))
            oldTask.setTitle(task.getTitle());
        if (task.getDescription() != null && !Objects.equals(task.getDescription(), ""))
            oldTask.setDescription(task.getDescription());
        if (task.getStatus() != null && !Objects.equals(task.getStatus(), ""))
            oldTask.setStatus(task.getStatus());
        if (task.getFinishedDate() != null && !Objects.equals(task.getFinishedDate(), ""))
            oldTask.setFinishedDate(task.getFinishedDate());
        if (task.getStartDate() != null && !Objects.equals(task.getStartDate(), ""))
            oldTask.setStartDate(task.getStartDate());
        if (task.getAnnotation() != null && !Objects.equals(task.getAnnotation(), ""))
            oldTask.setAnnotation(task.getAnnotation());
        if (task.getPriority() != null && !Objects.equals(task.getPriority(), null))
            oldTask.setPriority(task.getPriority());
        if (task.getDifficulty() != null && !Objects.equals(task.getDifficulty(), ""))
            oldTask.setDifficulty(task.getDifficulty());
        ShowTask showTask = new ShowTask(oldTask);
        // Comprobamos que la fecha de inicio y la de hoy no sea posterior a la de finalización.
        if (!showTask.getStartDate().isBefore(showTask.getFinishedDate()))
            throw new BadRequestException("Task start date is after task finished date.", Response.created(URI.create("/api/v1/tasks")).status(400).build());
        if (!showTask.getFinishedDate().isBefore(LocalDate.now()))
            throw new BadRequestException("Task finished date is before today.", Response.created(URI.create("/api/v1/tasks")).status(400).build());
        // Actualizamos la tarea en la base de datos.
        taskService.updateTask(oldTask);
        // Devolvemos la respuesta.
        return Response.noContent().build();
    }

    @DELETE
    @Path("/{taskId}")
    public Response deleteTask(@PathParam("taskId") Long taskId,
                               @Context UriInfo uriInfo) {
        // Buscamos la tarea en la base de datos.
        Task task = taskService.findTaskById(taskId);
        // Comprobamos si se encuentra la tarea en la base de datos.
        if (task == null)
            throw new NotFoundException("Task not found.", Response.created(uriInfo.getRequestUri()).status(404).build());
        // Eliminamos la tarea de la base de datos.
        taskService.deleteTask(task);
        // Devolvemos la respuesta.
        return Response.noContent().build();
    }
}



