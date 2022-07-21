package com.todolist.repositories;

import com.todolist.entity.User;
import jakarta.ws.rs.BadRequestException;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;


public class UserRepository {


    private static UserRepository instance = null;
    private final GroupUserRepository groupUserRepository;
    private final UserTaskRepository userTaskRepository;

    private TreeSet<User> users;


    private Long generatedId = 0L;

    public UserRepository() {
        users = new TreeSet<>();
        generateData();
        groupUserRepository = GroupUserRepository.getInstance();
        userTaskRepository = UserTaskRepository.getInstance();
    }

    public static UserRepository getInstance() {
        instance = (instance == null) ? new UserRepository() : instance;
        return instance;
    }

    public void generateData() {
        save(User.of("Misco", "Jones", "miscosama@gmail.com", "https://es.web.img3.acsta.net/pictures/17/05/19/13/05/463219.jpg", "Ser celestial, nacido para ayudar", "mi casa"));
        save(User.of("El Pelón", "Calvo", "niunpelotonto@tortilla.ong", "http://pm1.narvii.com/6120/9cd70762280f430ded8158c06c287e82b84d0101_00.jpg", "Nacío en un día en el que el sol brillo de tal manera que dislumbró a los imples mortales", "3000 viviendas"));
        save(User.of("Yonatan", "Yostar", "jojito@gmail.com", "https://i.pinimg.com/originals/09/52/27/095227e83b41e44b8de3ba8e81efe2e1.jpg", "Solamente defender al mundo del caos", "La Tierra"));
        save(User.of("Kaeya", "Alberich", "tucopito@hotmal.com", "https://img-17.ccm2.net/M5IDYIxs4R9RmHBLCt9l-PWqYLc=/500x/eff2597a02394167920c9d1cf7945a3c/ccm-faq/C3.JPG", "Kaeya Alberich es el hijo adoptivo de los Ragnvindr, una familia magnate con muchas bodegas", "Khaenri'ah"));
        save(User.of("Aurelion", "Sol", "ElForjadorDeLasEstrellas@riot.com", "https://static.wikia.nocookie.net/yugiohenespanol/images/c/c4/Drag%C3%B3n_c%C3%B3smico_blazar.jpg/revision/latest/scale-to-width-down/1200?cb=20200201203300&path-prefix=es", "El ao shin que nunca salió", "En el espacio picha"));
        save(User.of("Paquito", "El Chocolatero", "kingafrica@us.es", "https://pbs.twimg.com/media/FNgG3rCXEAEng6B.jpg", "Fui sifu de Willy Wonka, el sabe todo gracias a mí", "ESPAÑA"));
    }

    public void deleteData() {
        generatedId = 0L;
        users.clear();
    }

    public List<User> findAll() {
        return new ArrayList<>(users);
    }

    public List<User> findAll(String order, Sort sort) {
        String nameMethod = "get" + order.substring(0, 1).toUpperCase() + order.substring(1);
        Method method;
        try {
            method = User.class.getMethod(nameMethod);
        } catch (NoSuchMethodException e) {
            throw new BadRequestException("Order not found");
        }
        return sort.sort(users, method);
    }

    public User findByIdUser(Long idUser) {
        return users.stream().filter(x -> x.getIdUser().equals(idUser)).findFirst().orElse(null);
    }


    public User save(User user) {
        user.setIdUser(generatedId++);
        if (users.add(user)) return user;
        else throw new BadRequestException("User already exists");
    }

    public User update(User newUser) {
        users = users.stream().map(user -> {
            if (newUser.getIdUser().equals(user.getIdUser())) return newUser;
            else return user;
        }).collect(Collectors.toCollection(TreeSet::new));
        return newUser;
    }

    public User delete(User user) {
        if (!users.remove(user)) throw new BadRequestException("User not found");
        groupUserRepository.deleteAll(groupUserRepository.findByIdUser(user.getIdUser()));
        userTaskRepository.deleteAll(userTaskRepository.findByIdUser(user.getIdUser()));
        return user;
    }
}
