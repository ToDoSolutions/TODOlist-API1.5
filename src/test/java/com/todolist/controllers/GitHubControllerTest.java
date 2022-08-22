package com.todolist.controllers;

import com.todolist.converters.DateFilterConverter;
import com.todolist.converters.DifficultyConverter;
import com.todolist.converters.NumberFilterConverter;
import com.todolist.converters.StatusConverter;
import com.todolist.dtos.ShowTask;
import com.todolist.dtos.ShowUser;
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

public class GitHubControllerTest extends JerseyTest {

    @Override
    protected Application configure() {
        return new ResourceConfig(
                // Resource
                GitHubController.class, UserController.class, TaskController.class,
                // Converters
                StatusConverter.class, DateFilterConverter.class, DifficultyConverter.class, NumberFilterConverter.class,
                // Exceptions
                HandleServerException.class, HandleBadRequestException.class, HandleNotFoundException.class, HandleConstraintViolationException.class
        );
    }

    /*  -- USER -- */
    // GetSolo
    @Test
    public void testGetSoloUserFine() {
        final ShowUser responseMsg = target().path("github/alesanfe")
                .request().get(ShowUser.class);
        assertNotNull("No se ha encontrado el dato.", responseMsg);
    }

    @Test
    public void testGetSoloUserNotFound() {
        try {
            target().path("github/AAA").request().get(ShowUser.class);
        } catch (BadRequestException e) {
            assertTrue("No se ha encontrado el dato.", true);
        }
    }

    // Add
    @Test
    public void testAddUserFine() {
        final ShowUser responseMsg = target().path("github/alesanfe")
                .request().post(Entity.entity(new ShowUser(), "application/json"), ShowUser.class);
        assertNotNull("No se ha encontrado el dato.", responseMsg);
        final ShowUser[] responseMsg2 = target().path("users")
                .queryParam("name", "Alejandro")
                .request().get(ShowUser[].class);
        assertNotNull("No se ha encontrado el dato.", responseMsg2);
    }

    @Test
    public void testAddUserNotFound() {
        try {
            target().path("github/AAA").request().post(Entity.entity(new ShowUser(), "application/json"), ShowUser.class);
        } catch (BadRequestException e) {
            assertTrue("No se ha encontrado el dato.", true);
        }
    }

    /*  -- REPO -- */
    // GetAll
    @Test
    public void testGetAllRepoFine() {
        final ShowTask[] responseMsg = target().path("github/repos/alesanfe")
                .request().get(ShowTask[].class);
        assertTrue("No se han encontrado datos.", responseMsg != null && responseMsg.length > 0);
    }

    @Test
    public void testGetAllRepoNotFound() {
        try {
            target().path("github/repos/AAA").request().get(ShowTask[].class);
        } catch (BadRequestException e) {
            assertTrue("No se han encontrado datos.", true);
        }
    }

    // GetSolo
    @Test
    public void testGetSoloRepoFine() {
        final ShowTask responseMsg = target().path("github/repos/alesanfe/todolist")
                .request().get(ShowTask.class);
        assertNotNull("No se ha encontrado el dato.", responseMsg);
    }

    @Test
    public void testGetSoloRepoNotFound() {
        try {
            target().path("github/repos/alesanfe/AAA").request().get(ShowTask.class);
        } catch (BadRequestException e) {
            assertTrue("No se ha encontrado el dato.", true);
        }
    }

    // Add
    @Test
    public void testAddRepoFine() {
        final ShowTask responseMsg = target().path("github/alesanfe/TODOlist-API")
                .queryParam("finishedDate", "2023-01-01")
                .request().post(Entity.entity(new ShowTask(), "application/json"), ShowTask.class);
        assertNotNull("No se ha encontrado el dato.", responseMsg);
        final ShowTask[] responseMsg2 = target().path("tasks")
                .queryParam("title", "TODOlist-API")
                .request().get(ShowTask[].class);
        assertNotNull("No se ha encontrado el dato.", responseMsg2);
    }

    @Test
    public void testAddRepoNotFound() {
        try {
            target().path("github/alesanfe/AAA").request().post(Entity.entity(new ShowTask(), "application/json"), ShowTask.class);
        } catch (BadRequestException e) {
            assertTrue("No se ha encontrado el dato.", true);
        }
    }
}
