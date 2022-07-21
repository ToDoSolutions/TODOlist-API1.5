package com.todolist.controllers;

import com.todolist.converters.DateFilterConverter;
import com.todolist.converters.DifficultyConverter;
import com.todolist.converters.NumberFilterConverter;
import com.todolist.converters.StatusConverter;
import com.todolist.dtos.ShowGroup;
import com.todolist.entity.Group;
import com.todolist.exceptions.*;
import com.todolist.repositories.GroupRepository;
import com.todolist.repositories.TaskRepository;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Application;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class GroupResourceTest extends JerseyTest {

    @Override
    protected Application configure() {
        return new ResourceConfig(
                // Resource
                GroupResource.class,
                // Converters
                StatusConverter.class, DateFilterConverter.class, DifficultyConverter.class, NumberFilterConverter.class,
                // Exceptions
                HandleServerException.class, HandleBadRequestException.class, HandleNotFoundException.class, HandleConstraintViolationException.class
        );
    }

    // GetAll
    @Test
    public void testGetAllFine() {
        final ShowGroup[] responseMsg = target().path("groups").request().get(ShowGroup[].class);
        assertEquals(3, responseMsg.length);
    }


    @Test
    public void testGetAllWithQueryParamFine() {
        GroupRepository.getInstance().deleteData();
        GroupRepository.getInstance().generateData();
        final ShowGroup[] responseMsg = target().path("groups")
                .queryParam("fieldsGroup", "idGroup,name")
                .queryParam("name", "Pepe")
                .queryParam("description", "Solo quieren ver el mundo arder")
                .queryParam("numTasks", ">0")
                .queryParam("createdDate", "=2006-10-12")
                .request().get(ShowGroup[].class);
        assertEquals(1, responseMsg.length);
    }


    @Test
    public void testGetAllWrong() {
        try {
            target().path("groups")
                    .queryParam("createdDate", "---")
                    .request().get(ExceptionResponse.class);
        } catch (BadRequestException e) {
            assertEquals(400, e.getResponse().getStatus());
        }
    }


    // GetSolo
    @Test
    public void testGetSoloFine() {
        final ShowGroup responseMsg = target().path("groups/0").request().get(ShowGroup.class);
        assertEquals("Pepe", responseMsg.getName());
    }


    @Test
    public void testGetSoloNotFound() {
        try {
            target().path("groups/3").request().get(ExceptionResponse.class);
        } catch (NotFoundException e) {
            assertEquals(404, e.getResponse().getStatus());
        }
    }

    // Put
    @Test
    public void testPutFine() {
        Group group = Group.of(
                "Pepe",
                "Solo quieren ver el mundo arder",
                "2006-10-12"
        );
        group.setIdGroup(2L);
        target().path("groups").request().put(Entity.json(group));
        final ShowGroup responseMsg = target().path("groups/2").request().get(ShowGroup.class);
        assertEquals("Pepe", responseMsg.getName());
    }

    @Test
    public void testPutWithOutID() {
        Group group = Group.of(
                "Pepe",
                "Solo quieren ver el mundo arder",
                "2006-10-12"
        );
        try {
            target().path("groups").request().put(Entity.json(group));
        } catch (BadRequestException e) {
            assertEquals(400, e.getResponse().getStatus());
        }
    }

    @Test
    public void testPutWithWrongDate() {
        Group group = Group.of(
                "Pepe",
                "Solo quieren ver el mundo arder",
                "2006-10-12"
        );
        group.setIdGroup(2L);
        try {
            target().path("groups").request().put(Entity.json(group));
        } catch (BadRequestException e) {
            assertEquals(400, e.getResponse().getStatus());
        }
    }

    // Post
    @Test
    public void testPostFine() {
        Group group = Group.of(
                "Pepe",
                "Solo quieren ver el mundo arder",
                "2006-10-12"
        );
        target().path("groups").request().post(Entity.json(group));
        final ShowGroup responseMsg = target().path("groups/3").request().get(ShowGroup.class);
        assertEquals("Pepe", responseMsg.getName());
    }

    @Test
    public void testPostWithOutName() {
        Group group = Group.of(
                null,
                "Solo quieren ver el mundo arder",
                "2006-10-12"
        );
        try {
            target().path("groups").request().post(Entity.json(group));
        } catch (BadRequestException e) {
            assertEquals(400, e.getResponse().getStatus());
        }
    }

    @Test
    public void testPostWithWrongDate() {
        Group group = Group.of(
                null,
                "Solo quieren ver el mundo arder",
                "---"
        );
        try {
            target().path("groups").request().post(Entity.json(group));
        } catch (BadRequestException e) {
            assertEquals(400, e.getResponse().getStatus());
        }
    }

    // Delete
    @Test
    public void testDeleteFine() {
        target().path("groups/0").request().delete();
        try {
            target().path("groups/0").request().get(ExceptionResponse.class);
        } catch (NotFoundException e) {
            assertEquals(404, e.getResponse().getStatus());
        }
    }

    @Test
    public void testDeleteNotFound() {
        try {
            target().path("groups/3").request().delete();
        } catch (NotFoundException e) {
            assertEquals(404, e.getResponse().getStatus());
        }
    }

}
