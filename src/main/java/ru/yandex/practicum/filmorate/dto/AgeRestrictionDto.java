package ru.yandex.practicum.filmorate.dto;

import lombok.Data;

@Data
public class AgeRestrictionDto {
    private final Long id;
    private final String name;
    private final String description;
}
