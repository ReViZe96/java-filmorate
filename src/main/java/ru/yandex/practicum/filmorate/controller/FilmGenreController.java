package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.FilmGenreDto;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RestController
@RequestMapping("genres")
@RequiredArgsConstructor
public class FilmGenreController {

    private final FilmService filmService;

    @GetMapping
    public List<FilmGenreDto> getAllGenres() {
        return filmService.getAllGenres();
    }

    @GetMapping("/{id}")
    public FilmGenreDto getGenreById(@PathVariable Long id) {
        return filmService.getGenreById(id);
    }

}


