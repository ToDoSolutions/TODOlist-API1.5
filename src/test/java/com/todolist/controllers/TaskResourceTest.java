package com.todolist.controllers;

import com.todolist.converters.DateFilterConverter;
import com.todolist.converters.DifficultyConverter;
import com.todolist.converters.NumberFilterConverter;
import com.todolist.converters.StatusConverter;
import com.todolist.dtos.Difficulty;
import com.todolist.dtos.ShowTask;
import com.todolist.dtos.Status;
import com.todolist.entity.Task;
import com.todolist.exceptions.*;
import com.todolist.repositories.TaskRepository;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Application;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TaskResourceTest extends JerseyTest {

    @Override
    protected Application configure() {
        return new ResourceConfig(
                // Resource
                TaskResource.class,
                // Converters
                StatusConverter.class, DateFilterConverter.class, DifficultyConverter.class, NumberFilterConverter.class,
                // Exceptions
                HandleServerException.class, HandleBadRequestException.class, HandleNotFoundException.class, HandleConstraintViolationException.class
        );
    }


    // GetAll
    @Test
    public void testGetAllFine() {
        final ShowTask[] responseMsg = target().path("tasks").request().get(ShowTask[].class);
        assertEquals(7, responseMsg.length);
    }

    @Test
    public void testGetAllWithQueryParamFine() {
        TaskRepository.getInstance().deleteData();
        TaskRepository.getInstance().generateData();
        final ShowTask[] responseMsg = target().path("tasks")
                .queryParam("fields", "idTask,title,description,status,difficulty,date")
                .queryParam("title", "Vacaciones")
                .queryParam("description", "Quiero vacaciones")
                .queryParam("annotation", "Vacaciones")
                .queryParam("status", "DRAFT")
                .queryParam("startDate", "=2020-01-01")
                .queryParam("finishedDate", "=2020-01-31")
                .queryParam("priority", ">=3")
                .queryParam("difficulty", "HARDCORE")
                .queryParam("duration", ">=10")
                .request().get(ShowTask[].class);
        for (ShowTask task : responseMsg) {
            System.out.println(task.getIdTask());
            System.out.println(task.getTitle());
            System.out.println(task.getDescription());
            System.out.println(task.getAnnotation());
            System.out.println(task.getStatus());
            System.out.println(task.getDifficulty());
            System.out.println(task.getStartDate());
            System.out.println(task.getFinishedDate());
            System.out.println(task.getPriority());

        }
        assertEquals(1, responseMsg.length);
    }

    @Test
    public void testGetAllWrong() {
        try {
            target().path("tasks")
                    .queryParam("startDate", "---")
                    .request().get(ExceptionResponse.class);
        } catch (BadRequestException e) {
            assertEquals(400, e.getResponse().getStatus());
        }
    }

    // GetSolo
    @Test
    public void testGetSoloFine() {
        final ShowTask responseMsg = target().path("tasks/0").request().get(ShowTask.class);
        assertEquals("Vacaciones", responseMsg.getTitle());
    }

    @Test
    public void testGetSoloNotFound() {
        try {
            target().path("tasks/8").request().get(ExceptionResponse.class);
        } catch (NotFoundException e) {
            assertEquals(404, e.getResponse().getStatus());
        }
    }

    // Put
    @Test
    public void testPutFine() {
        Task task = Task.of("Vacaciones",
                "Quiero vacaciones",
                "Vacaciones",
                Status.DRAFT.toString(),
                "2020-01-31",
                "2020-01-01",
                5L,
                Difficulty.HARDCORE.toString());
        task.setIdTask(6L);
        target().path("tasks").request().put(Entity.json(task));
        final ShowTask responseMsg = target().path("tasks/6").request().get(ShowTask.class);
        assertEquals("Vacaciones", responseMsg.getTitle());
    }

    @Test
    public void testPutWithOutID() {
        Task task = Task.of("Vacaciones",
                "Quiero vacaciones",
                "Vacaciones",
                Status.DRAFT.toString(),
                "2020-01-31",
                "2020-01-01",
                5L,
                Difficulty.HARDCORE.toString());
        try {
            target().path("tasks").request().put(Entity.json(task));
        } catch (BadRequestException e) {
            assertEquals(400, e.getResponse().getStatus());
        }
    }

    @Test
    public void testPutWithWrongDate() {
        Task task = Task.of("Vacaciones",
                "Quiero vacaciones",
                "Vacaciones",
                Status.DRAFT.toString(),
                "---",
                "---",
                5L,
                Difficulty.HARDCORE.toString());
        task.setIdTask(6L);
        try {
            target().path("tasks").request().put(Entity.json(task));
        } catch (BadRequestException e) {
            assertEquals(400, e.getResponse().getStatus());
        }
    }

    // Post
    @Test
    public void testPostFine() {
        Task task = Task.of("Vacaciones",
                "Quiero vacaciones",
                "Vacaciones",
                Status.DRAFT.toString(),
                "2020-01-31",
                "2020-01-01",
                5L,
                Difficulty.HARDCORE.toString());
        target().path("tasks").request().post(Entity.json(task));
        final ShowTask responseMsg = target().path("tasks/7").request().get(ShowTask.class);
        assertEquals("Vacaciones", responseMsg.getTitle());
    }

    @Test
    public void testPostWithOutTitle() {
        Task task = Task.of(null,
                "Quiero vacaciones",
                "Vacaciones",
                Status.DRAFT.toString(),
                "2020-01-31",
                "2020-01-01",
                5L,
                Difficulty.HARDCORE.toString());
        try {
            target().path("tasks").request().post(Entity.json(task));
        } catch (BadRequestException e) {
            assertEquals(400, e.getResponse().getStatus());
        }
    }

    @Test
    public void testPostWithWrongDate() {
        Task task = Task.of("Vacaciones",
                "Quiero vacaciones",
                "Vacaciones",
                Status.DRAFT.toString(),
                "---",
                "---",
                5L,
                Difficulty.HARDCORE.toString());
        try {
            target().path("tasks").request().post(Entity.json(task));
        } catch (BadRequestException e) {
            assertEquals(400, e.getResponse().getStatus());
        }
    }

    // Delete
    @Test
    public void testDeleteFine() {
        target().path("tasks/0").request().delete();
        try {
            target().path("tasks/0").request().get(ExceptionResponse.class);
        } catch (NotFoundException e) {
            assertEquals(404, e.getResponse().getStatus());
        }
    }

    @Test
    public void testDeleteNotFound() {
        try {
            target().path("tasks/8").request().delete();
        } catch (NotFoundException e) {
            assertEquals(404, e.getResponse().getStatus());
        }
    }
}
