package ru.yandex.practicum.filmorate.model;

import lombok.Getter;

@Getter
public enum FilmGenre {

    COMEDY(1L, "Comedy", "Комедия"),
    DRAMA(2L, "Drama", "Драмма"),
    CARTOON(3L, "Cartoon", "Мультфильм"),
    THRILLER(4L, "Thriller", "Триллер"),
    ACTION(5L, "Action", "Боевик"),
    SCIFI(6L, "Science fiction", "Научная фантастика"),
    DOCUMENTARY(7L, "Documentary movie", "Документальное кино");

    private final Long id;
    private final String englishName;
    private final String russianName;

    FilmGenre(Long id, String englishName, String russianName) {
        this.id = id;
        this.englishName = englishName;
        this.russianName = russianName;
    }
}
