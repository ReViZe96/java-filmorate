package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.FilmGenreDto;
import ru.yandex.practicum.filmorate.exception.LikesManipulationException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mappers.MpaMapper;
import ru.yandex.practicum.filmorate.mappers.FilmGenreMapper;
import ru.yandex.practicum.filmorate.mappers.FilmMapper;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.time.LocalDate;
import java.util.*;

@Service
public class FilmService {

    private final Logger log = LoggerFactory.getLogger(FilmService.class);

    private LocalDate mostEarlierReleaseDate = LocalDate.of(1895, 12, 28);

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    //Значения @Qualifier для записи данных в оперативную память
    //filmInMemoryStorage
    //userInMemoryStorage
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }


    public List<FilmDto> getAllFilms() {
        return filmStorage.getAll().stream().map(FilmMapper::mapToFilmDto).toList();
    }

    public FilmDto getFilmById(Long id) {
        Optional<Film> film = filmStorage.getFilmById(id);
        if (film.isPresent()) {
            log.info("Фильм {} найден", film.get().getName());
            return film.map(FilmMapper::mapToFilmDto).get();
        } else {
            throw new NotFoundException("Фильм с id = " + id + " не найден");
        }
    }

    public FilmDto addFilm(Film newFilm) {
        Optional<Film> film = null;
        boolean isFilmValid = isFilmValid(newFilm);
        if (isFilmValid) {
            log.info("Добавляемый фильм {} валиден", newFilm.getName());
            film = filmStorage.addFilm(newFilm);
            log.info("В систему добавлен новый фильм под названием: {}", film.get().getName());
        }
        return film.map(FilmMapper::mapToFilmDto).get();
    }

    public FilmDto updateFilm(Film updatedFilm) {
        if (updatedFilm.getId() == null) {
            throw new ValidationException("Id должен быть указан");
        }
        if (!filmStorage.isFilmExist(updatedFilm.getId())) {
            throw new NotFoundException("Фильм с id = " + updatedFilm.getId() + " не найден");
        } else {
            Optional<Film> film = filmStorage.updateFilm(updatedFilm);
            return film.map(FilmMapper::mapToFilmDto).get();
        }
    }

    public FilmDto addLikeToFilm(Long filmId, Long userId) {
        isLikeCanBeAdded(filmId, userId);
        return filmStorage.addLike(filmId, userStorage.getUserById(userId).get()).map(FilmMapper::mapToFilmDto).get();
    }

    public void deleteLikeFromFilm(Long filmId, Long userId) {
        isLikeCanBeDeleted(filmId, userId);
        filmStorage.removeLike(filmId, userStorage.getUserById(userId).get());
    }

    public List<FilmDto> getMostPopularFilms(int count) {
        if (count <= 0) {
            throw new ValidationException("Размер выборки должен быть больше нуля");
        }
        return filmStorage.getAll().stream().sorted(Film::compareTo).limit(count).map(FilmMapper::mapToFilmDto).toList();
    }

    public List<FilmGenreDto> getAllGenres() {
        return filmStorage.getAllGenres().stream().map(FilmGenreMapper::mapToFilmGenreDto).toList();
    }

    public FilmGenreDto getGenreById(Long genreId) {
        Optional<FilmGenre> genre = filmStorage.getGenreById(genreId);
        if (genre.isEmpty()) {
            throw new NotFoundException("Жанр с id = " + genreId + " не найден");
        } else {
            return genre.map(FilmGenreMapper::mapToFilmGenreDto).get();
        }
    }

    public List<MpaDto> getAllMpas() {
        return filmStorage.getAllMpas().stream().map(MpaMapper::mapToMpaDto).toList();
    }

    public MpaDto getMpaById(Long mpaId) {
        Optional<Mpa> mpa = filmStorage.getMpaById(mpaId);
        if (mpa.isEmpty()) {
            throw new NotFoundException("Возрастное ограничение с id = " + mpaId + " не найдено");
        } else {
            return mpa.map(MpaMapper::mapToMpaDto).get();
        }
    }


    private boolean isFilmValid(Film film) throws ValidationException {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название добавляемого фильма не должно быть пустым");
        }
        if (film.getDescription().length() > 200) {
            throw new ValidationException("Превышена максимальная длина описания добавляемого фильма");
        }
        if (film.getReleaseDate().isBefore(mostEarlierReleaseDate)) {
            throw new ValidationException("Добавляемый фильм не мог выйти в прокат до создания кинематографа");
        }
        if (film.getDuration() <= 0) {
            throw new ValidationException("продолжительность добавляемого фильма должна быть положительным числом");
        }
        return true;
    }

    private boolean isLikeCanBeAdded(Long filmId, Long userId) {
        if (filmId == null) {
            throw new ValidationException("Id понравившегося фильма должен быть указан");
        }
        if (userId == null) {
            throw new ValidationException("Id пользователя, лайкающего фильм, должен быть указан");
        }
        if (!filmStorage.isFilmExist(filmId)) {
            throw new NotFoundException("Фильм с id = " + filmId + " не найден");
        }
        if (!userStorage.isUserExist(userId)) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        List<User> filmLikeIds = filmStorage.getFilmLikeIds(filmId);
        if (filmLikeIds.contains(userStorage.getUserById(userId).get())) {
            throw new LikesManipulationException("Пользователь с d = " + userId + " уже лайкнул фильм с id = " + filmId);
        }
        return true;
    }

    private boolean isLikeCanBeDeleted(Long filmId, Long userId) {
        if (filmId == null) {
            throw new ValidationException("Id разонравившегося фильма, должен быть указан");
        }
        if (userId == null) {
            throw new ValidationException("Id пользователя, убирающего свой лайк, должен быть указан");
        }
        if (!filmStorage.isFilmExist(filmId)) {
            throw new NotFoundException("Фильм с id = " + filmId + " не найден");
        }
        if (!userStorage.isUserExist(userId)) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        List<User> filmLikeIds = filmStorage.getFilmLikeIds(filmId);
        if (!filmLikeIds.contains(userStorage.getUserById(userId).get())) {
            throw new LikesManipulationException("Фильм с id = " + filmId + " не состоит в избранном у пользователя " +
                    "с id = " + userId);
        }
        return true;
    }

}
