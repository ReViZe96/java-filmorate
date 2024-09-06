package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;

@RestController
@RequestMapping("films")
public class FilmController {

    private final static Logger log = LoggerFactory.getLogger(FilmController.class);

    private HashMap<Long, Film> films = new HashMap<>();
    private LocalDate mostEarlierReleaseDate = LocalDate.of(1895, 12, 28);

    @GetMapping
    public Collection<Film> getAll() {
        return films.values();
    }

    @PostMapping
    public Film add(@RequestBody Film newFilm) {
        try {
            if (newFilm.getName() == null) {
                throw new ValidationException("Название добавляемого фильма не должно быть пустым");
            }

            if (newFilm.getDescription().length() > 200) {
                throw new ValidationException("Превышена максимальная длина описания добавляемого фильма");
            }

            if (newFilm.getReleaseDate().isBefore(mostEarlierReleaseDate)) {
                throw new ValidationException("Добавляемый фильм не мог выйти в прокат до создания кинематографа");
            }

            if (newFilm.getDuration().toSeconds() < 0) {
                throw new ValidationException("продолжительность добавляемого фильма должна быть положительным числом");
            }

            newFilm.setId(getNextId());
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        films.put(newFilm.getId(), newFilm);
        log.info("В систему добавлен новый фильм под названием: {}", newFilm.getName());
        return newFilm;
    }


    @PutMapping
    public Film update(@RequestBody Film updatedFilm) {
        Film oldFilm = null;
        try {
            if (updatedFilm.getId() == null) {
                throw new ValidationException("Id должен быть указан");
            }

            if (films.containsKey(updatedFilm.getId())) {
                oldFilm = films.get(updatedFilm.getId());

                oldFilm.setName(updatedFilm.getName());
                oldFilm.setDescription(updatedFilm.getDescription());
                oldFilm.setReleaseDate(updatedFilm.getReleaseDate());
                oldFilm.setDuration(updatedFilm.getDuration());
                log.info("В системе обновлены данные о фильме под названием: {}", updatedFilm.getName());
                return oldFilm;
            } else {
                throw new ValidationException("Фильм с id = " + updatedFilm.getId() + " не найден");
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return oldFilm;
    }


    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
