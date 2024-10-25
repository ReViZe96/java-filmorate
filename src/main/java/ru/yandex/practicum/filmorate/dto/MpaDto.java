package ru.yandex.practicum.filmorate.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MpaDto {
    private Long id;
    private String name;

    //для расширенной модели
    //private String description;
}