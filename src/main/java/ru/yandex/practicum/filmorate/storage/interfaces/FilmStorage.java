package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface FilmStorage {

    Collection<Film> getAll();

    Optional<Film> getFilmById(Long id);

    void addFilm(Film film);

    boolean isFilmExist(Long filmId);

    long getNextId();

    Set<Long> getFilmLikeIds(Long id);

    Film addLike(Long filmId, Long userId);

    void removeLike(Long filmId, Long userId);
}
