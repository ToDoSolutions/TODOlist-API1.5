package com.todolist.controllers;

import com.todolist.converters.DateFilterConverter;
import com.todolist.converters.DifficultyConverter;
import com.todolist.converters.NumberFilterConverter;
import com.todolist.converters.StatusConverter;
import com.todolist.dtos.ShowUser;
import com.todolist.entity.User;
import com.todolist.exceptions.*;
import com.todolist.repositories.UserRepository;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Application;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UserControllerTest extends JerseyTest {

    @Override
    protected Application configure() {
        return new ResourceConfig(
                // Resource
                UserController.class,
                // Converters
                StatusConverter.class, DateFilterConverter.class, DifficultyConverter.class, NumberFilterConverter.class,
                // Exceptions
                HandleServerException.class, HandleBadRequestException.class, HandleNotFoundException.class, HandleConstraintViolationException.class
        );
    }

    // GetAll
    @Test
    public void testGetAllFine() {
        final ShowUser[] responseMsg = target().path("users").request().get(ShowUser[].class);
        assertEquals(6, responseMsg.length);
    }


    @Test
    public void testGetAllWithQueryParamFine() {
        UserRepository.getInstance().deleteData();
        UserRepository.getInstance().generateData();
        final ShowUser[] responseMsg = target().path("users")
                .queryParam("fieldsUser", "idUser,name,surname,email")
                .queryParam("name", "Misco")
                .queryParam("surname", "Jones")
                .queryParam("email", "miscosama@gmail.com")
                .queryParam("location", "mi casa")
                .request().get(ShowUser[].class);
        assertEquals(1, responseMsg.length);
    }

    @Test
    public void testGetAllWrong() {
        try {
            target().path("users")
                    .queryParam("taskCompleted", "---")
                    .request().get(ExceptionResponse.class);
        } catch (BadRequestException e) {
            assertEquals(400, e.getResponse().getStatus());
        }
    }

    // GetSolo
    @Test
    public void testGetSoloFine() {
        final ShowUser responseMsg = target().path("users/0").request().get(ShowUser.class);
        assertEquals("Misco", responseMsg.getName());
    }


    @Test
    public void testGetSoloNotFound() {
        UserRepository.getInstance().deleteData();
        UserRepository.getInstance().generateData();
        try {
            target().path("users/6").request().get(ExceptionResponse.class);
        } catch (NotFoundException e) {
            assertEquals(404, e.getResponse().getStatus());
        }
    }


    // Put
    @Test
    public void testPutFine() {
        User user = User.of(
                "Misco",
                "Jones",
                "miscosama@gmail.com",
                "https://es.web.img3.acsta.net/pictures/17/05/19/13/05/463219.jpg",
                "Ser celestial, nacido para ayudar",
                "mi casa");
        user.setIdUser(5L);
        target().path("users").request().put(Entity.json(user));
        final ShowUser responseMsg = target().path("users/5").request().get(ShowUser.class);
        assertEquals("Misco", responseMsg.getName());
    }

    @Test
    public void testPutWithOutID() {
        User user = User.of(
                "Misco",
                "Jones",
                "miscosama@gmail.com",
                "https://es.web.img3.acsta.net/pictures/17/05/19/13/05/463219.jpg",
                "Ser celestial, nacido para ayudar",
                "mi casa");
        try {
            target().path("users").request().put(Entity.json(user));
        } catch (BadRequestException e) {
            assertEquals(400, e.getResponse().getStatus());
        }
    }

    @Test
    public void testPutWithWrongEmail() {
        User user = User.of(
                "Misco",
                "Jones",
                "miscosamamail.com",
                "https://es.web.img3.acsta.net/pictures/17/05/19/13/05/463219.jpg",
                "Ser celestial, nacido para ayudar",
                "mi casa");
        user.setIdUser(5L);
        try {
            target().path("users").request().put(Entity.json(user));
        } catch (BadRequestException e) {
            assertEquals(400, e.getResponse().getStatus());
        }
    }

    // Post
    @Test
    public void testPostFine() {
        UserRepository.getInstance().deleteData();
        UserRepository.getInstance().generateData();
        User user = User.of(
                "Misco",
                "Jones",
                "miscosama@gmail.com",
                "https://es.web.img3.acsta.net/pictures/17/05/19/13/05/463219.jpg",
                "Ser celestial, nacido para ayudar",
                "mi casa");
        target().path("users").request().post(Entity.json(user));
        final ShowUser responseMsg = target().path("users/6").request().get(ShowUser.class);
        assertEquals("Misco", responseMsg.getName());
    }


    @Test
    public void testPostWithOutName() {
        User user = User.of(
                null,
                "Jones",
                "miscosama@gmail.com",
                "https://es.web.img3.acsta.net/pictures/17/05/19/13/05/463219.jpg",
                "Ser celestial, nacido para ayudar",
                "mi casa");
        try {
            target().path("users").request().post(Entity.json(user));
        } catch (BadRequestException e) {
            assertEquals(400, e.getResponse().getStatus());
        }
    }


    @Test
    public void testPostWithWrongAvatar() {
        User user = User.of(
                null,
                "Jones",
                "miscosama@gmail.com",
                "Pepe",
                "Ser celestial, nacido para ayudar",
                "mi casa");
        try {
            target().path("users").request().post(Entity.json(user));
        } catch (BadRequestException e) {
            assertEquals(400, e.getResponse().getStatus());
        }
    }

    // Delete
    @Test
    public void testDeleteFine() {
        target().path("users/0").request().delete();
        try {
            target().path("users/0").request().get(ExceptionResponse.class);
        } catch (NotFoundException e) {
            assertEquals(404, e.getResponse().getStatus());
        }
    }

    @Test
    public void testDeleteNotFound() {
        try {
            target().path("users/8").request().delete();
        } catch (NotFoundException e) {
            assertEquals(404, e.getResponse().getStatus());
        }
    }
}
