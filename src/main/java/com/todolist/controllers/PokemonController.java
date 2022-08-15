package com.todolist.controllers;


import com.todolist.dtos.ShowTask;
import com.todolist.services.PokemonService;
import com.todolist.services.TaskService;
import jakarta.validation.executable.ValidateOnExecution;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@ValidateOnExecution
@Path("/pokemon")
@Produces(MediaType.APPLICATION_JSON)
public class PokemonController {

    protected static PokemonController instance = null; // La instancia inicialmente no existe, se crea al ejecutar .getInstance().
    final TaskService taskService; // Para poder trabajar con los datos
    final PokemonService pokemonService; // Para poder trabajar con los datos


    public PokemonController() {
        taskService = TaskService.getInstance();
        pokemonService = PokemonService.getInstance();
    }

    public static PokemonController getInstance() {
        instance = (instance == null) ? new PokemonController() : instance; // Creamos una instancia si no existe.
        return instance;
    }

    @GET
    @Path("/{name}")
    public Response getPokemon(
            @PathParam("name") String name,
            @QueryParam("status") String status,
            @QueryParam("finishedDate") String finishedDate,
            @QueryParam("startDate") String startDate,
            @QueryParam("days") Integer days,
            @QueryParam("priority") Long priority) {
        return Response.ok(new ShowTask(pokemonService.findPokemonTaskByName(name, status, finishedDate, priority, days, startDate))).build();
    }

    @POST
    @Path("/{name}")
    public Response addPokemon(
            @PathParam("name") String name,
            @QueryParam("status") String status,
            @QueryParam("finishedDate") String finishedDate,
            @QueryParam("startDate") String startDate,
            @QueryParam("days") Integer days,
            @QueryParam("priority") Long priority) {
        return Response.ok(new ShowTask(taskService.saveTask(pokemonService.findPokemonTaskByName(name, status, finishedDate, priority, days, startDate)))).build();
    }

    @GET
    public Response getAllPokemons() {
        return Response.ok(pokemonService.findAllPokemonTask().stream().map(ShowTask::new)).build();
    }
}
