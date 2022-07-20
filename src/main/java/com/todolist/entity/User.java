package com.todolist.entity;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@EqualsAndHashCode(of = {"idUser"})
@NoArgsConstructor
public class User implements Comparable<User> {

    private Long idUser;
    @Size(max = 50, message = "The name is too long.")
    private String name;
    @Size(max = 50, message = "The surname is too long.")
    private String surname;
    @Email(message = "The email is invalid.")
    private String email;
    @Pattern(regexp = "^(https?|ftp|file)://[-a-zA-Z\\d+&@#/%?=~_|!:,.;]*[-a-zA-Z\\d+&@#/%=~_|]", message = "The avatar is invalid.")
    private String avatar;
    @Size(max = 500, message = "The bio is too long.")
    private String bio;
    @Size(max = 50, message = "The location is too long.")
    private String location;

    public static User of(String name, String surname, String email, String avatar, String bio, String location) {
        User user = new User();
        user.setName(name);
        user.setSurname(surname);
        user.setEmail(email);
        user.setAvatar(avatar);
        user.setBio(bio);
        user.setLocation(location);
        return user;
    }

    @Override
    public int compareTo(User o) {
        return this.getIdUser().compareTo(o.getIdUser());
    }


}
