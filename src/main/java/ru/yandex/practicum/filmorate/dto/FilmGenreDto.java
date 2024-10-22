package ru.yandex.practicum.filmorate.dto;

import lombok.Data;

@Data
public class FilmGenreDto {
    private final Long id;
    private final String englishName;
    private final String russianName;
}
