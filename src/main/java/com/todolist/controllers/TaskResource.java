package com.todolist.controllers;


import com.todolist.dtos.Difficulty;
import com.todolist.dtos.ShowTask;
import com.todolist.dtos.Status;
import com.todolist.entity.Task;
import com.todolist.filters.DateFilter;
import com.todolist.filters.NumberFilter;
import com.todolist.services.TaskService;
import jakarta.validation.*;
import jakarta.validation.executable.ValidateOnExecution;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Path("/tasks")
public class TaskResource {

    protected static TaskResource instance = null;
    final TaskService taskService; // Para poder trabajar con los datos
    // private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator(); // Arreglar algún día.

    public TaskResource() {
        System.out.println("TaskResource");
        taskService = TaskService.getInstance();
        System.out.println("TaskResource taskService: " + taskService);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllTasks(@QueryParam("order") @DefaultValue("+idTask") String order,
                                @QueryParam("limit") @DefaultValue("-1") Integer limit,
                                @QueryParam("offset") Integer offset,
                                @QueryParam("fields") @DefaultValue(ShowTask.ALL_ATTRIBUTES) String fields,
                                @QueryParam("title") String title,
                                @QueryParam("status") @DefaultValue("null") Status status,
                                @QueryParam("startDate") @DefaultValue("null") DateFilter startDate,
                                @QueryParam("finishedDate") @DefaultValue("null") DateFilter finishedDate,
                                @QueryParam("priority") @DefaultValue("null") NumberFilter priority,
                                @QueryParam("difficulty") @DefaultValue("null") Difficulty difficulty,
                                @QueryParam("duration") @DefaultValue("null") NumberFilter duration) {
        List<ShowTask> result = new ArrayList<>();
        String propertyOrder = order.charAt(0) == '+' || order.charAt(0) == '-' ? order.substring(1) : order;
        System.out.println("4");
        if (!(Arrays.stream(ShowTask.ALL_ATTRIBUTES.split(",")).anyMatch(prop -> prop.equalsIgnoreCase(propertyOrder))))
            throw new BadRequestException("The order is invalid.");
        System.out.println("3");
        if (!(Arrays.stream(fields.split(",")).allMatch(field -> ShowTask.ALL_ATTRIBUTES.toLowerCase().contains(field.toLowerCase()))))
            throw new BadRequestException("The fields are invalid.");
        System.out.println("2");
        List<ShowTask> tasks = new ArrayList<>(taskService.findAllShowTasks(order));
        System.out.println("1");
        if (limit == -1) limit = tasks.size() - 1;
        int start = offset == null || offset < 1 ? 0 : offset - 1; // Donde va a comenzar.
        int end = limit == null || limit > tasks.size() ? tasks.size() : start + limit; // Donde va a terminar.
        for (int i = start; i < end; i++) {
            ShowTask task = tasks.get(i);
            if (task != null &&
                    (title == null || task.getTitle().contains(title)) &&
                    (status == null || task.getStatus().equals(status)) &&
                    (startDate == null || startDate.isValid(task.getStartDate())) &&
                    (finishedDate == null || finishedDate.isValid(task.getStartDate())) &&
                    (priority == null || priority.isValid(task.getPriority())) &&
                    (difficulty == null || task.getDifficulty().equals(difficulty)) &&
                    (duration == null || duration.isValid(task.getDuration())))
                result.add(task);
        }

        return Response.ok(result.stream().map(task -> task.getFields(fields == null ? ShowTask.ALL_ATTRIBUTES : fields)).collect(Collectors.toList())).build();
    }


    @GET
    @Path("/{taskId}")
    public Response getTask(@PathParam("taskId") Long taskId,
                            @QueryParam("fields") @DefaultValue(ShowTask.ALL_ATTRIBUTES) String fields) {
        Task task = taskService.findTaskById(taskId);
        if (task == null)
            throw new NotFoundException("Task not found."); // Comprobamos si se encuentra el objeto en la base de datos chapucera.
        return Response.ok(new ShowTask(task).getFields(fields == null ? ShowTask.ALL_ATTRIBUTES : fields)).build();
    }

    @POST
    @Consumes("application/json")
    public Response addTask(@Context UriInfo uriInfo, @Valid Task task) {

        if (task == null) throw new BadRequestException("Task is null.");
        if (task.getTitle() == null || task.getTitle().isEmpty())
            throw new BadRequestException("Task title is null or empty.");
        if (task.getDescription() == null || task.getDescription().isEmpty())
            throw new BadRequestException("Task description is null or empty.");
        if (task.getFinishedDate() == null || task.getFinishedDate().isEmpty())
            throw new BadRequestException("Task start date is null.");
        if (task.getStartDate() == null) task.setStartDate(LocalDate.now().format(DateTimeFormatter.ISO_DATE));
        ShowTask showTask = new ShowTask(task); // Creamos un objeto ShowTask para poder mostrar la tarea.
        if (!showTask.getStartDate().isBefore(showTask.getFinishedDate()))
            throw new BadRequestException("Task start date is after task finished date.");
        if (showTask.getFinishedDate().isBefore(LocalDate.now()))
            throw new BadRequestException("Task finished date is before today.");
        task = taskService.saveTask(task); // Añadimos la tarea a la base de datos.
        showTask = new ShowTask(task);
        // Builds the response. Returns the task the has just been added.
        UriBuilder ub = uriInfo.getAbsolutePathBuilder().path(this.getClass(), "getTask");
        URI uri = ub.build(showTask.getIdTask());
        Response.ResponseBuilder resp = Response.created(uri);
        resp.entity(showTask);
        return resp.build();
    }

    @PUT
    @Consumes("application/json")
    public Response updateTask(@Valid Task task) {
        Task oldTask = taskService.findTaskById(task.getIdTask());
        if (oldTask == null)
            throw new NullPointerException("The task with idTask " + task.getIdTask() + " does not exist.");
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
        // Set<ConstraintViolation<Task>> errors = validator.validate(oldTask);
        //if (!errors.isEmpty())
        //    throw new ConstraintViolationException(errors);
        ShowTask showTask = new ShowTask(oldTask);
        if (!showTask.getStartDate().isBefore(showTask.getFinishedDate()))
            throw new BadRequestException("Task start date is after task finished date.");
        if (showTask.getFinishedDate().isBefore(LocalDate.now()))
            throw new BadRequestException("Task finished date is before today.");
        taskService.saveTask(oldTask);
        return Response.noContent().build();
    }

    @DELETE
    @Path("/{taskId}")
    public Response deleteTask(@PathParam("taskId") Long taskId) {
        Task task = taskService.findTaskById(taskId);

        if (task == null)
            throw new NotFoundException("Task not found."); // Comprobamos si se encuentra el objeto en la base de datos chapucera.

        taskService.deleteTask(task); // Eliminamos la tarea de la base de datos.
        return Response.noContent().build();
    }
}



