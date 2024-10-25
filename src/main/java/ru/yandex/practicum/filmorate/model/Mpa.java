package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Mpa {

    private final Long id;
    private final String name;

    //для расширенной модели
    //private String description;

}
