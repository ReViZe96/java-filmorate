package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.AgeRestrictionDto;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RestController
@RequestMapping("mpa")
@RequiredArgsConstructor
public class AgeRestrictionController {

    private final FilmService filmService;

    @GetMapping
    public List<AgeRestrictionDto> getAllAgeRestrictions() {
        return filmService.getAllAgeRestrictions();
    }

    @GetMapping("/{id}")
    public AgeRestrictionDto getAgeRestrictionById(@PathVariable Long id) {
        return filmService.getAgeRestrictionById(id);
    }

}
