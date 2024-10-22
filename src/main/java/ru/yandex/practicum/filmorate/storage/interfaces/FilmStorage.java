package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.AgeRestriction;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface FilmStorage {

    Collection<Film> getAll();

    Optional<Film> getFilmById(Long id);

    Optional<Film> addFilm(Film film);

    Optional<Film> updateFilm(Film film);

    boolean isFilmExist(Long filmId);

    Set<Long> getFilmLikeIds(Long id);

    Optional<Film> addLike(Long filmId, Long userId);

    void removeLike(Long filmId, Long userId);

    Collection<FilmGenre> getAllGenres();

    Optional<FilmGenre> getGenreById(Long genreId);

    Collection<AgeRestriction> getAllAgeRestrictions();

    Optional<AgeRestriction> getAgeRestrictionById(Long ageRestrictionId);
}
