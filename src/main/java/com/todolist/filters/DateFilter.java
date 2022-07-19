package com.todolist.filters;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.regex.Pattern;


@AllArgsConstructor
@Getter
public class DateFilter {

    private Boolean isGreater;
    private Boolean isLess;
    private Boolean isEqual;
    private LocalDate date;

    public static DateFilter parse(String filterWithDate) {
        if (Pattern.compile("[<>=]\\d{4}-\\d{2}-\\d{2}").matcher(filterWithDate).matches()) {
            String filter = filterWithDate.charAt(0) + "";
            LocalDate localDate = LocalDate.parse(filterWithDate.substring(1), DateTimeFormatter.ISO_DATE);
            switch (filter) {
                case "<":
                    return new DateFilter(false, true, false, localDate);
                case ">":
                    return new DateFilter(true, false, false, localDate);
                case "=":
                    return new DateFilter(false, false, true, localDate);
                default:
                    throw new IllegalArgumentException("The filter is not valid and it should have a parameter filter (<,>,=,<=,=<,>=,=>,==,!=,<>,><) and a date with the format YYYY-MM-DD.");
            }
        } else if (Pattern.compile("[<>=]{2}\\d{4}-\\d{2}-\\d{2}").matcher(filterWithDate).matches()) {
            String filter = filterWithDate.charAt(0) + "" + filterWithDate.charAt(1);
            LocalDate localDate = LocalDate.parse(filterWithDate.substring(2), DateTimeFormatter.ISO_DATE);
            switch (filter) {
                case "<=":
                case "=<":
                    return new DateFilter(false, true, true, localDate);
                case ">=":
                case "=>":
                    return new DateFilter(true, false, true, localDate);
                case "==":
                    return new DateFilter(false, false, true, localDate);
                case "!=":
                case "<>":
                case "><":
                    return new DateFilter(true, true, false, localDate);
                default:
                    throw new IllegalArgumentException("The filter is not valid and it should have a parameter filter (<,>,=,<=,=<,>=,=>,==,!=,<>,><) and a date with the format YYYY-MM-DD.");
            }
        } else {
            throw new IllegalArgumentException("The filter is not valid and it should have a parameter filter (<,>,=,<=,=<,>=,=>,==,!=,<>,><) and a date with the format YYYY-MM-DD.");
        }
    }

    public boolean isValid(LocalDate date) {
        if (Objects.isNull(date))
            return false;
        else if (Boolean.TRUE.equals(isGreater)) // newDate > date
            return date.isAfter(this.date);
        else if (Boolean.TRUE.equals(isLess)) // newDate < date
            return date.isBefore(this.date);
        else if (Boolean.TRUE.equals(isEqual)) // newDate == date
            return date.isEqual(this.date);
        return false;
    }
}
