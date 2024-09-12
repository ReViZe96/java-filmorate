package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class Film {

    protected Long id;
    protected String name;
    protected String description;
    protected LocalDate releaseDate;
    protected Long duration;

}
