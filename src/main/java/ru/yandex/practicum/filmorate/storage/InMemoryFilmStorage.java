package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;

import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Logger log = LoggerFactory.getLogger(InMemoryFilmStorage.class);

    private HashMap<Long, Film> films = new HashMap<>();

    @Override
    public Collection<Film> getAll() {
        return films.values();
    }

    @Override
    public Optional<Film> getFilmById(Long id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public void addFilm(Film film) {
        if (film.getLikes() == null) {
            film.setLikes(new HashSet<>());
        }
        films.put(film.getId(), film);
    }

    @Override
    public boolean isFilmExist(Long filmId) {
        return films.containsKey(filmId);
    }

    @Override
    public Set<Long> getFilmLikeIds(Long id) {
        return films.get(id).getLikes();
    }

    @Override
    public Film addLike(Long filmId, Long userId) {
        Film film = films.get(filmId);
        Set<Long> likes = film.getLikes();
        likes.add(userId);
        film.setLikes(likes);
        log.info("Лайк пользователя с id = {} добавлен фильму {}", userId, film.getName());
        return film;
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        Film film = films.get(filmId);
        Set<Long> likes = film.getLikes();
        likes.remove(userId);
        film.setLikes(likes);
        log.info("Лайк пользователя с id = {} удален у фильма {}", userId, film.getName());
    }

    @Override
    public long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }


    //для тестов - временная мера
    public Collection<Film> getSavedFilms() {
        return this.films.values();
    }

}


