package com.todolist.dtos;

import java.util.regex.Pattern;

public enum Difficulty {
    SLEEP,
    EASY,
    MEDIUM,
    HARD,
    HARDCORE,
    I_WANT_TO_DIE;

    public static Difficulty parse(String difficulty) {
        String difficultyLowerCase = difficulty.toLowerCase();
        if (difficultyLowerCase.equals("sleep"))
            return SLEEP;
        else if (difficultyLowerCase.equals("easy"))
            return EASY;
        else if (difficultyLowerCase.equals("medium"))
            return MEDIUM;
        else if (difficultyLowerCase.equals("hard"))
            return HARD;
        else if (difficultyLowerCase.equals("hardcore"))
            return HARDCORE;
        else if (Pattern.compile("i[_ ]want[_ ]to[_ ]die").matcher(difficultyLowerCase).matches())
            return I_WANT_TO_DIE;
        else {
            return null;
        }

    }
}
