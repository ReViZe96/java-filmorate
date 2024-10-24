package ru.yandex.practicum.filmorate.mappers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.FilmGenreDto;
import ru.yandex.practicum.filmorate.model.FilmGenre;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FilmGenreMapper {

    public static FilmGenreDto mapToFilmGenreDto(FilmGenre filmGenre) {
//        return new FilmGenreDto(filmGenre.getId(), filmGenre.getEnglishName(), filmGenre.getRussianName());
        return new FilmGenreDto(filmGenre.getId(), filmGenre.getName());
    }

}
