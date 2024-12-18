package ru.yandex.practicum.filmorate.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FilmGenreDto {
    private Long id;
    private String name;
}
