package com.todolist.controllers;

import com.todolist.converters.DateFilterConverter;
import com.todolist.converters.DifficultyConverter;
import com.todolist.converters.NumberFilterConverter;
import com.todolist.converters.StatusConverter;
import com.todolist.dtos.ShowTask;
import com.todolist.exceptions.HandleBadRequestException;
import com.todolist.exceptions.HandleConstraintViolationException;
import com.todolist.exceptions.HandleNotFoundException;
import com.todolist.exceptions.HandleServerException;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Application;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class PokemonControllerTest extends JerseyTest {

    @Override
    protected Application configure() {
        return new ResourceConfig(
                // Resource
                PokemonController.class,
                // Converters
                StatusConverter.class, DateFilterConverter.class, DifficultyConverter.class, NumberFilterConverter.class,
                // Exceptions
                HandleServerException.class, HandleBadRequestException.class, HandleNotFoundException.class, HandleConstraintViolationException.class
        );
    }

    // GetAll
    @Test
    public void testGetAllFine() {
        final ShowTask[] responseMsg = target().path("pokemon").request().get(ShowTask[].class);
        assertTrue("No se han encontrado datos.", responseMsg != null && responseMsg.length > 0);
    }

    // GetSolo
    @Test
    public void testGetSoloFine() {
        final ShowTask responseMsg = target().path("pokemon/Wingull")
                .queryParam("days", 4).request().get(ShowTask.class);
        assertTrue("No se ha encontrado el dato.", responseMsg != null);
    }

    @Test
    public void testGetSoloNotFound() {
        try {
            target().path("pokemon/AAA").request().get(ShowTask.class);
        } catch (BadRequestException e) {
            assertTrue("No se ha encontrado el dato.", true);
        }
    }

    @Test
    public void testGetSoloLowerCase() {
        final ShowTask responseMsg = target().path("pokemon/wingull")
                .queryParam("days", 4).request().get(ShowTask.class);
        assertTrue("No se ha encontrado el dato.", responseMsg != null);
    }

    // Add
    @Test
    public void testAddFine() {
        final ShowTask responseMsg = target().path("pokemon/wingull")
                .queryParam("days", 4).request().post(Entity.json(null), ShowTask.class);
        assertTrue("No se ha encontrado el dato.", responseMsg != null);
    }
}
