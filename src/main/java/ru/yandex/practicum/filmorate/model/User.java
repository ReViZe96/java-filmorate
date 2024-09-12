package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class User {

    protected Long id;
    protected String email;
    protected String login;
    protected String name;
    protected LocalDate birthday;

}
