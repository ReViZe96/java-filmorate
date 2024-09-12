package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

public class FilmControllerTest {

    private FilmController filmController = new FilmController();

    @Test
    public void shouldAddedFilm() {
        Film MrNobody = Film.builder()
                .name("Господин Никто")
                .description("Хороший фильм")
                .releaseDate(LocalDate.of(2009, 9, 18))
                .duration(8280L)
                .build();

        Assertions.assertTrue(filmController.getSavedFilms().isEmpty());
        Assertions.assertDoesNotThrow(() -> filmController.add(MrNobody));
        Assertions.assertEquals(filmController.getSavedFilms().size(), 1);
        Assertions.assertTrue(filmController.getSavedFilms().contains(MrNobody));
    }

    @Test
    public void shouldNotAddedFilmWithEmptyOrNullName() {
        Film filmWithEmptyName = Film.builder()
                .name("")
                .description("Фильм с пустым названием")
                .releaseDate(LocalDate.now())
                .duration(360L)
                .build();
        Film filmWithNullName = Film.builder()
                .name(null)
                .description("Фильм без названия")
                .releaseDate(LocalDate.now())
                .duration(720L)
                .build();

        Assertions.assertThrowsExactly(ResponseStatusException.class, () -> filmController.add(filmWithEmptyName));
        Assertions.assertThrowsExactly(ResponseStatusException.class, () -> filmController.add(filmWithNullName));
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
                .build();

        Assertions.assertThrowsExactly(ResponseStatusException.class, () -> filmController.add(filmWithLongDescription));
    }

    @Test
    public void shouldNotAddedFilmWithAncientReleaseDate() {
        Film filmCreatedBeforeLumersBrothers = Film.builder()
                .name("Диафильм")
                .description("Поезд не приехал")
                .releaseDate(LocalDate.of(1895, 12, 27))
                .duration(180L)
                .build();

        Assertions.assertThrowsExactly(ResponseStatusException.class, () -> filmController.add(filmCreatedBeforeLumersBrothers));
    }

    @Test
    public void shouldNotAddedFilmWithNegativeOrNullDuration() {
        Film filmWithNegativeDuration = Film.builder()
                .name("Интерстеллар 2.0")
                .description("Фильм, просматривающийся в прошлое")
                .releaseDate(LocalDate.now())
                .duration(-7000L)
                .build();
        Film filmWithNullDuration = Film.builder()
                .name("Самый короткий фильм")
                .description("Фильм, состоящий из 11 кадров")
                .releaseDate(LocalDate.now())
                .duration(0L)
                .build();

        Assertions.assertThrowsExactly(ResponseStatusException.class, () -> filmController.add(filmWithNegativeDuration));
        Assertions.assertThrowsExactly(ResponseStatusException.class, () -> filmController.add(filmWithNullDuration));
    }


    @Test
    public void shouldUpdateFilm() {
        Film addingFilm = Film.builder()
                .name("Создаваемый фильм")
                .description("Фильм, который потом обновим")
                .releaseDate(LocalDate.now())
                .duration(1000L)
                .build();

        Film addedFilm = filmController.add(addingFilm);

        Film updatingFilm = Film.builder()
                .id(addedFilm.getId())
                .name("Обновленный фильм")
                .description("Фильм, который обновили")
                .releaseDate(LocalDate.of(1996, 10, 5))
                .duration(10000L)
                .build();

        Assertions.assertEquals(filmController.getSavedFilms().size(), 1);
        Assertions.assertTrue(filmController.getSavedFilms().contains(addedFilm));

        Assertions.assertDoesNotThrow(() -> filmController.update(updatingFilm));
        Assertions.assertEquals(filmController.getSavedFilms().size(), 1);
        Assertions.assertTrue(filmController.getSavedFilms().contains(updatingFilm));
    }

    @Test
    public void shouldNotUpdatedFilmWithoutId() {
        Film filmWithoutId = Film.builder()
                .name("Неизвестный фильм")
                .description("Фильм, без Id, который хочется обновить")
                .releaseDate(LocalDate.now())
                .duration(2000L)
                .build();

        Assertions.assertThrowsExactly(ResponseStatusException.class, () -> filmController.update(filmWithoutId));
    }

}
