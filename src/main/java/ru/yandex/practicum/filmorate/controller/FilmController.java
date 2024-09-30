package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("films")
public class FilmController {

    private FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }


    @GetMapping
    public Collection<Film> getAllFilms() {
        return filmService.getAllFilms();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable Long id) {
        return filmService.getFilmById(id);
    }

    @PostMapping
    public Film addFilm(@RequestBody Film newFilm) {
        return filmService.addFilm(newFilm);
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film updatedFilm) {
        return filmService.updateFilm(updatedFilm);
    }

    //пользователь ставит лайк фильму
    @PutMapping("/{id}/like/{userId}")
    public Film addLikeToFilm(@PathVariable Long id, @PathVariable Long userId) {
        return filmService.addLikeToFilm(id, userId);

    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLikeFromFilm(@PathVariable Long id, @PathVariable Long userId) {
        filmService.deleteLikeFromFilm(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getMostPopularFilms(@RequestParam(defaultValue = "10") int count) {
        return filmService.getMostPopularFilms(count);
    }

}
