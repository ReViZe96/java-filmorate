package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.mappers.FilmMapper;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;

import java.util.*;

@Component("filmInMemoryStorage")
public class InMemoryFilmStorage implements FilmStorage {

    private final Logger log = LoggerFactory.getLogger(InMemoryFilmStorage.class);

    private HashMap<Long, Film> films = new HashMap<>();
    private HashMap<Long, FilmGenre> genres = new HashMap<>();
    private HashMap<Long, Mpa> mpas = new HashMap<>();

    @Override
    public Collection<Film> getAll() {
        return films.values();
    }

    @Override
    public Optional<Film> getFilmById(Long id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public Optional<Film> addFilm(Film film) {
        if (film.getLikes() == null) {
            film.setLikes(new ArrayList<>());
        }
        film.setId(getNextId());
        films.put(film.getId(), film);
        return Optional.of(film);
    }

    @Override
    public Optional<Film> updateFilm(Film updatedFilm) {
        Film oldFilm = films.get(updatedFilm.getId());
        log.info("Обновляемый фильм {} найден", oldFilm.getName());
        oldFilm.setName(updatedFilm.getName());
        oldFilm.setDescription(updatedFilm.getDescription());
        oldFilm.setReleaseDate(updatedFilm.getReleaseDate());
        oldFilm.setDuration(updatedFilm.getDuration());
        if (updatedFilm.getLikes() == null) {
            oldFilm.setLikes(new ArrayList<>());
        } else {
            oldFilm.setLikes(updatedFilm.getLikes());
        }
        log.info("В системе обновлены данные о фильме под названием: {}", updatedFilm.getName());
        return Optional.of(oldFilm);
    }

    @Override
    public boolean isFilmExist(Long filmId) {
        return films.containsKey(filmId);
    }

    @Override
    public List<User> getFilmLikeIds(Long id) {
        return films.get(id).getLikes();
    }

    @Override
    public Optional<Film> addLike(Long filmId, User user) {
        Film film = films.get(filmId);
        List<User> likes = film.getLikes();
        likes.add(user);
        film.setLikes(likes);
        log.info("Лайк пользователя с {} добавлен фильму {}", user.getLogin(), film.getName());
        return Optional.of(film);
    }

    @Override
    public void removeLike(Long filmId, User user) {
        Film film = films.get(filmId);
        List<User> likes = film.getLikes();
        likes.remove(user);
        film.setLikes(likes);
        log.info("Лайк пользователя {} удален у фильма {}", user.getLogin(), film.getName());
    }

    @Override
    public Collection<FilmGenre> getAllGenres() {
        return genres.values();
    }

    @Override
    public Optional<FilmGenre> getGenreById(Long genreId) {
        return Optional.ofNullable(genres.get(genreId));
    }

    @Override
    public Collection<Mpa> getAllMpas() {
        return mpas.values();
    }

    @Override
    public Optional<Mpa> getMpaById(Long mpaId) {
        return Optional.ofNullable(mpas.get(mpaId));
    }


    public long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }


    //для тестов - временная мера
    public Collection<FilmDto> getSavedFilms() {
        return this.films.values().stream().map(FilmMapper::mapToFilmDto).toList();
    }

}


