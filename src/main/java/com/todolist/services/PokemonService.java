package com.todolist.services;

import com.todolist.entity.Task;
import com.todolist.entity.pokemon.Pokemon;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.client.ClientBuilder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PokemonService {

    private static PokemonService instance = null;
    final String startUrl = "https://pokemonapiaiss.lm.r.appspot.com/api/pokemons/";

    public static PokemonService getInstance() {
        instance = (instance == null) ? new PokemonService() : instance;
        return instance;
    }

    private static String getAnnotation(Pokemon pokemon) {
        if (pokemon.getLegend()) {
            return "Be careful, you will need a masterball!!!";
        } else {
            if (pokemon.getLegend()) {
                return "easy peasy lemon squeezy, take one pokeball";
            } else if (getAvgStats(pokemon) < 100) {
                return "mmmh, you will nead some pokeballs";
            } else if (getAvgStats(pokemon) < 150) {
                return "uffff, you must take a great a amount of superballs";
            } else if (getAvgStats(pokemon) < 200) {
                return "Yisus, if you do not catch dozens of super balls, you will not be able to catch it.";
            } else {
                return "LMFAO, take the entire Pokemon Center in your bag";
            }
        }
    }

    private static String getDifficulty(Pokemon pokemon) {
        if (Boolean.TRUE.equals(pokemon.getLegend())) {
            return "I_WANT_TO_DIE";
        } else {
            if (getAvgStats(pokemon) < 100) {
                return "EASY";
            } else if (getAvgStats(pokemon) < 150) {
                return "MEDIUM";
            } else if (getAvgStats(pokemon) < 200) {
                return "HARD";
            } else {
                return "HARDCORE";
            }
        }
    }

    private static Integer getAvgStats(Pokemon pokemon) {
        return (pokemon.getHp() + pokemon.getAttack() + pokemon.getDefense()) / 3;
    }

    public Pokemon[] findAllPokemon() {
        String url = startUrl;
        try {
            return ClientBuilder.newClient().target(url).request().get(Pokemon[].class);
        } catch (Exception e) {
            throw new BadRequestException("Error while getting pokemon.");
        }
    }

    public Pokemon findPokemonByName(String name) {
        String url = startUrl + name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
        try {
            return ClientBuilder.newClient().target(url).request().get(Pokemon.class);
        } catch (Exception e) {
            throw new BadRequestException("Error while getting pokemon.");
        }
    }

    public Task findPokemonTaskByName(String name, String status, String finishedDate, Long priority, Integer days, String startDate) {
        return pokemonIntoTask(findPokemonByName(name), status, finishedDate, priority, days, startDate);
    }

    public List<Task> findAllPokemonTask() {
        return Arrays.stream(findAllPokemon()).map(this::pokemonIntoTask).collect(Collectors.toList());
    }

    public Task pokemonIntoTask(Pokemon pokemon) {
        Task task = new Task();
        StringBuilder types = new StringBuilder();
        if (pokemon.getType2() == null)
            types.append(pokemon.getType1());
        else
            types.append(pokemon.getType1()).append("/").append(pokemon.getType2());
        task.setTitle("Catch: " + pokemon.getName());
        task.setDescription("Type pokemon: " + types);
        task.setStartDate(LocalDate.now().toString());
        task.setFinishedDate(LocalDate.now().toString());
        task.setAnnotation(getAnnotation(pokemon));
        task.setDifficulty(getDifficulty(pokemon));
        return task;
    }

    public Task pokemonIntoTask(Pokemon pokemon, String status, String finishedDate, Long Priority, Integer days, String startDate) {
        Task task = new Task();
        StringBuilder types = new StringBuilder();
        if (finishedDate != null && days != null)
            throw new BadRequestException("You can't set finished date and days at the same time.");
        else if (finishedDate == null && days == null)
            throw new BadRequestException("You must set finished date or days.");
        if (pokemon.getType2() == null)
            types.append(pokemon.getType1());
        else
            types.append(pokemon.getType1()).append("/").append(pokemon.getType2());
        task.setTitle("Catch: " + pokemon.getName());
        task.setDescription("Type pokemon: " + types);
        task.setAnnotation(getAnnotation(pokemon));
        task.setStatus(status);
        task.setFinishedDate(finishedDate == null ? startDate == null ? LocalDate.now().plusDays(days).toString() : LocalDate.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE).plusDays(days).toString() : finishedDate);
        task.setStartDate(startDate == null ? LocalDate.now().toString() : startDate);
        task.setPriority(Priority);
        task.setDifficulty(getDifficulty(pokemon));
        return task;
    }
}
