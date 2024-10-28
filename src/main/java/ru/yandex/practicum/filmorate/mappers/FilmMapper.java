package ru.yandex.practicum.filmorate.mappers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FilmMapper {

    public static FilmDto mapToFilmDto(Film film) {
        return FilmDto.builder()
                .id(film.getId())
                .name(film.getName())
                .description(film.getDescription())
                .releaseDate(film.getReleaseDate())
                .duration(film.getDuration())
                .likes(film.getLikes() != null ? film.getLikes().stream().map(UserMapper::mapToUserDto).toList() : null)
                .genres(film.getGenres() != null ? film.getGenres().stream().map(FilmGenreMapper::mapToFilmGenreDto).toList() : null)
                .mpa(film.getMpa() != null ? Optional.of(film.getMpa()).map(MpaMapper::mapToMpaDto).get() : null)
                .build();
    }

}
