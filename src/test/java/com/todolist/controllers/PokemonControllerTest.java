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
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Application;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
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
<<<<<<<HEAD
        final ShowTask[] responseMsg = target().path("pokemon")
                .request().get(ShowTask[].class);
=======
        final ShowTask[] responseMsg = target().path("pokemon").request().get(ShowTask[].class);
>>>>>>>f084247764a602892254a043ff300d1c698f57ab
        assertTrue("No se han encontrado datos.", responseMsg != null && responseMsg.length > 0);
    }

    // GetSolo
    @Test
    public void testGetSoloFine() {
        final ShowTask responseMsg = target().path("pokemon/Wingull")
                .queryParam("days", 4).request().get(ShowTask.class);
        assertNotNull("No se ha encontrado el dato.", responseMsg);
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
        assertNotNull("No se ha encontrado el dato.", responseMsg);
    }

    // Add
    @Test
    public void testAddFine() {
        final ShowTask responseMsg = target().path("pokemon/wingull")
                .queryParam("days", 4).request().post(Entity.json(null), ShowTask.class);
        assertNotNull("No se ha encontrado el dato.", responseMsg);
    }
}
