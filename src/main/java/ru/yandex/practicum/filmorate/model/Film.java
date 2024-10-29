package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class Film implements Comparable<Film> {

    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Long duration;
    private List<User> likes;
    private List<FilmGenre> genres;
    private Mpa mpa;

    @Override
    public int compareTo(Film film) {
        if (this.getLikes().size() == film.getLikes().size()) {
            return 0;
        } else if (this.getLikes().size() > film.getLikes().size()) {
            return -1;
        } else {
            return 1;
        }
    }

}
