package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mappers.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

public class FilmControllerTest {

    private InMemoryFilmStorage filmStorage = new InMemoryFilmStorage();
    private InMemoryUserStorage userStorage = new InMemoryUserStorage();
    private FilmService filmService = new FilmService(filmStorage, userStorage);
    private FilmController filmController = new FilmController(filmService);

    @Test
    public void shouldAddedFilm() {
        Film mrNobody = Film.builder()
                .name("Господин Никто")
                .description("Хороший фильм")
                .releaseDate(LocalDate.of(2009, 9, 18))
                .duration(8280L)
                .likes(new ArrayList<>())
                .build();

        Assertions.assertTrue(filmStorage.getSavedFilms().isEmpty());
        Assertions.assertDoesNotThrow(() -> filmController.addFilm(mrNobody));
        Assertions.assertEquals(filmStorage.getSavedFilms().size(), 1);
        Assertions.assertTrue(filmStorage.getSavedFilms().contains(Optional.of(mrNobody).map(FilmMapper::mapToFilmDto).get()));
    }

    @Test
    public void shouldNotAddedFilmWithEmptyOrNullName() {
        Film filmWithEmptyName = Film.builder()
                .name("")
                .description("Фильм с пустым названием")
                .releaseDate(LocalDate.now())
                .duration(360L)
                .likes(new ArrayList<>())
                .build();
        Film filmWithNullName = Film.builder()
                .name(null)
                .description("Фильм без названия")
                .releaseDate(LocalDate.now())
                .duration(720L)
                .likes(new ArrayList<>())
                .build();

        Assertions.assertThrowsExactly(ValidationException.class, () -> filmController.addFilm(filmWithEmptyName));
        Assertions.assertThrowsExactly(ValidationException.class, () -> filmController.addFilm(filmWithNullName));
    }

    @Test
    public void shouldNotAddedFilmWithMore200LengthDescription() {
        Film filmWithLongDescription = Film.builder()
                .name("Титаник")
                .description("Фильм в котором корабль плывет, плывет, плывет, плывет, плывет, плывет, плывет, плывет, " +
                        "плывет, плывет, плывет, плывет, плывет, плывет, плывет, плывет, плывет, плывет, а потом " +
                        "тонет, тонет, тонет, тонет, тонет, тонет, тонет, тонет, тонет, тонет, тонет, тонет, тонет. " +
                        "Основано на реальных событиях.")
                .releaseDate(LocalDate.now())
                .duration(7600L)
                .likes(new ArrayList<>())
                .build();

        Assertions.assertThrowsExactly(ValidationException.class, () -> filmController.addFilm(filmWithLongDescription));
    }

    @Test
    public void shouldNotAddedFilmWithAncientReleaseDate() {
        Film filmCreatedBeforeLumersBrothers = Film.builder()
                .name("Диафильм")
                .description("Поезд не приехал")
                .releaseDate(LocalDate.of(1895, 12, 27))
                .duration(180L)
                .likes(new ArrayList<>())
                .build();

        Assertions.assertThrowsExactly(ValidationException.class, () -> filmController.addFilm(filmCreatedBeforeLumersBrothers));
    }

    @Test
    public void shouldNotAddedFilmWithNegativeOrNullDuration() {
        Film filmWithNegativeDuration = Film.builder()
                .name("Интерстеллар 2.0")
                .description("Фильм, просматривающийся в прошлое")
                .releaseDate(LocalDate.now())
                .duration(-7000L)
                .likes(new ArrayList<>())
                .build();
        Film filmWithNullDuration = Film.builder()
                .name("Самый короткий фильм")
                .description("Фильм, состоящий из 11 кадров")
                .releaseDate(LocalDate.now())
                .duration(0L)
                .likes(new ArrayList<>())
                .build();

        Assertions.assertThrowsExactly(ValidationException.class, () -> filmController.addFilm(filmWithNegativeDuration));
        Assertions.assertThrowsExactly(ValidationException.class, () -> filmController.addFilm(filmWithNullDuration));
    }


    @Test
    public void shouldUpdateFilm() {
        Film addingFilm = Film.builder()
                .name("Создаваемый фильм")
                .description("Фильм, который потом обновим")
                .releaseDate(LocalDate.now())
                .duration(1000L)
                .likes(new ArrayList<>())
                .build();

        FilmDto addedFilm = filmController.addFilm(addingFilm);

        Film updatingFilm = Film.builder()
                .id(addedFilm.getId())
                .name("Обновленный фильм")
                .description("Фильм, который обновили")
                .releaseDate(LocalDate.of(1996, 10, 5))
                .duration(10000L)
                .likes(new ArrayList<>())
                .build();

        Assertions.assertEquals(filmStorage.getSavedFilms().size(), 1);
        Assertions.assertTrue(filmStorage.getSavedFilms().contains(addedFilm));

        Assertions.assertDoesNotThrow(() -> filmController.updateFilm(updatingFilm));
        Assertions.assertEquals(filmStorage.getSavedFilms().size(), 1);
        Assertions.assertTrue(filmStorage.getSavedFilms().contains(Optional.of(updatingFilm).map(FilmMapper::mapToFilmDto).get()));
    }

    @Test
    public void shouldNotUpdatedFilmWithoutId() {
        Film filmWithoutId = Film.builder()
                .name("Неизвестный фильм")
                .description("Фильм, без Id, который хочется обновить")
                .releaseDate(LocalDate.now())
                .duration(2000L)
                .likes(new ArrayList<>())
                .build();

        Assertions.assertThrowsExactly(ValidationException.class, () -> filmController.updateFilm(filmWithoutId));
    }

}
