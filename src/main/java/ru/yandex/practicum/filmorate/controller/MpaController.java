package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaController {

    private final FilmService filmService;

    @GetMapping
    public List<MpaDto> getAllMpas() {
        return filmService.getAllMpas();
    }

    @GetMapping("/{id}")
    public MpaDto getMpaById(@PathVariable Long id) {
        return filmService.getMpaById(id);
    }

}
