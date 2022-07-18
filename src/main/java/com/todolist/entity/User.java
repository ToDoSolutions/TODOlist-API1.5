package com.todolist.entity;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class User {

    private Long generateId = 0L;
    private Long idUser;
    @Size(max = 50, message = "The name is too long.")
    private String name;
    @Size(max = 50, message = "The surname is too long.")
    private String surname;
    @Size(max = 50, message = "The username is too long.")
    private String username;
    @Email(message = "The email is invalid.")
    private String email;
    @Pattern(regexp = "^(https?|ftp|file)://[-a-zA-Z\\d+&@#/%?=~_|!:,.;]*[-a-zA-Z\\d+&@#/%=~_|]", message = "The avatar is invalid.")
    private String avatar;
    @Size(max = 500, message = "The bio is too long.")
    private String bio;
    @Size(max = 50, message = "The location is too long.")
    private String location;

    public User() {
        this.idUser = generateId++;
    }

    public static User of(String name, String surname, String username, String email, String avatar, String bio, String location, String password) {
        User user = new User();
        user.setName(name);
        user.setSurname(surname);
        user.setUsername(username);
        user.setEmail(email);
        user.setAvatar(avatar);
        user.setBio(bio);
        user.setLocation(location);
        return user;
    }
}
