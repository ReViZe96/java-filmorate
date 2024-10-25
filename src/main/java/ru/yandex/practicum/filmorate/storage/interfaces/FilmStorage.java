package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    Collection<Film> getAll();

    Optional<Film> getFilmById(Long id);

    Optional<Film> addFilm(Film film);

    Optional<Film> updateFilm(Film film);

    boolean isFilmExist(Long filmId);

    List<User> getFilmLikeIds(Long id);

    Optional<Film> addLike(Long filmId, User user);

    void removeLike(Long filmId, User user);

    Collection<FilmGenre> getAllGenres();

    Optional<FilmGenre> getGenreById(Long genreId);

    Collection<Mpa> getAllMpas();

    Optional<Mpa> getMpaById(Long mpaId);

}
