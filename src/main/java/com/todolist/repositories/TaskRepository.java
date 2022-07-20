package com.todolist.repositories;


import com.todolist.dtos.Difficulty;
import com.todolist.dtos.Status;
import com.todolist.entity.Task;
import jakarta.ws.rs.BadRequestException;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;


public class TaskRepository {

    private static TaskRepository instance = null;
    private final UserTaskRepository userTaskRepository;
    private TreeSet<Task> tasks;

    private Long generatedId = 0L;

    public TaskRepository() {
        tasks = new TreeSet<>();
        generateData();
        userTaskRepository = UserTaskRepository.getInstance();

    }


    public static TaskRepository getInstance() {
        instance = (instance == null) ? new TaskRepository() : instance;
        return instance;
    }

    public void generateData() {
        save(Task.of("Vacaciones", "Quiero vacaciones", "Vacaciones", Status.DRAFT.toString(), "2020-01-31", "2020-01-01", 5l, Difficulty.HARDCORE.toString()));
        save(Task.of("Bronce", "Salir de bronce en el lol", "Quiero subir o mantenerme no bajar a hierro", Status.IN_PROGRESS.toString(), "2022-12-15", "2021-01-01", 5l, Difficulty.I_WANT_TO_DIE.toString()));
        save(Task.of("Aceitunas", "Comprar aceitunas sin hueso", "Sin hueso pero con pepinillo", Status.IN_PROGRESS.toString(), "2022-05-29", "2022-05-19", 2l, Difficulty.MEDIUM.toString()));
        save(Task.of("VIVA ER BETIS", "Ver el betis", "err Betiss", Status.DONE.toString(), "2022-08-08", "2022-08-07", 5l, Difficulty.EASY.toString()));
        save(Task.of("Entrenador Pokemon", "Completar la pokedex para el profesor Oak", "Hazte con todos, los 892...", Status.IN_REVISION.toString(), "2022-11-30", "2021-05-17", 3l, Difficulty.I_WANT_TO_DIE.toString()));
        save(Task.of("Comprar mando nuevo", "Nuevo mando para jugar elden ring", "Comprar uno a prueba de enfados", Status.DRAFT.toString(), "2022-06-06", "2022-01-19", 4l, Difficulty.MEDIUM.toString()));
        save(Task.of("¿Aprender Inglés? Na", "El inglés se enseña mal y punto", "Como aprendo el inglés si ni se el español", Status.CANCELLED.toString(), "2023-05-26", "2020-04-22", 0l, Difficulty.HARDCORE.toString()));
    }

    public void deleteData() {
        generatedId = 0L;
        tasks.clear();
    }

    public List<Task> findAll() {
        return new ArrayList<>(tasks);
    }

    public List<Task> findAll(String order, Sort sort) {
        String nameMethod = "get" + order.substring(0, 1).toUpperCase() + order.substring(1);
        Method method;
        try {
            method = Task.class.getMethod(nameMethod);
        } catch (NoSuchMethodException e) {
            throw new BadRequestException("Order not found");
        }
        return sort.sort(tasks, method);

    }

    public Task findByIdTask(Long idTask) {
        return tasks.stream().filter(x -> x.getIdTask().equals(idTask)).findFirst().orElse(null);
    }

    public Task save(Task task) {
        task.setIdTask(generatedId++);
        if (tasks.add(task)) {
            return task;
        } else throw new BadRequestException("Task already exists");
    }

    public Task update(Task newTask) {
        tasks = tasks.stream().map(task -> {
            if (newTask.getIdTask().equals(task.getIdTask())) return newTask;
            else return task;
        }).collect(Collectors.toCollection(TreeSet::new));
        return newTask;
    }

    public Task delete(Task task) {
        if (!tasks.remove(task)) throw new BadRequestException("Task not found");
        userTaskRepository.deleteAll(userTaskRepository.findByIdTask(task.getIdTask()));
        return task;
    }
}
