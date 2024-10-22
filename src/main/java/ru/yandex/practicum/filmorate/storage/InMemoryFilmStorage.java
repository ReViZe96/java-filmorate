package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.mappers.FilmMapper;
import ru.yandex.practicum.filmorate.model.AgeRestriction;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;

import java.util.*;

@Component("filmInMemoryStorage")
public class InMemoryFilmStorage implements FilmStorage {

    private final Logger log = LoggerFactory.getLogger(InMemoryFilmStorage.class);

    private HashMap<Long, Film> films = new HashMap<>();
    private HashMap<Long, FilmGenre> genres = new HashMap<>();
    private HashMap<Long, AgeRestriction> ageRestrictions = new HashMap<>();

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
            film.setLikes(new HashSet<>());
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
            oldFilm.setLikes(new HashSet<>());
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
    public Set<Long> getFilmLikeIds(Long id) {
        return films.get(id).getLikes();
    }

    @Override
    public Optional<Film> addLike(Long filmId, Long userId) {
        Film film = films.get(filmId);
        Set<Long> likes = film.getLikes();
        likes.add(userId);
        film.setLikes(likes);
        log.info("Лайк пользователя с id = {} добавлен фильму {}", userId, film.getName());
        return Optional.of(film);
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
    public Collection<FilmGenre> getAllGenres() {
        return genres.values();
    }

    @Override
    public Optional<FilmGenre> getGenreById(Long genreId) {
        return Optional.ofNullable(genres.get(genreId));
    }

    @Override
    public Collection<AgeRestriction> getAllAgeRestrictions() {
        return ageRestrictions.values();
    }

    @Override
    public Optional<AgeRestriction> getAgeRestrictionById(Long ageRestrictionId) {
        return Optional.ofNullable(ageRestrictions.get(ageRestrictionId));
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


