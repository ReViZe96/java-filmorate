package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FilmGenre {

    private final Long id;
    private final String name;
//    private final String englishName;
//    private final String russianName;

}
