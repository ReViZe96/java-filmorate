package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.storage.dal.FilmGenreDbStorage;
import ru.yandex.practicum.filmorate.storage.mappers.FilmGenreRowMapper;

import java.util.List;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({
        FilmGenreDbStorage.class,
        FilmGenreRowMapper.class,
})
public class FilmGenreDbStorageTest {

    private final FilmGenreDbStorage filmGenreDbStorage;

    @Test
    public void shouldGetAllGenresTest() {
        List<FilmGenre> allGenres = filmGenreDbStorage.getAll();
        Assertions.assertTrue(allGenres.size() == 6);
    }

    @Test
    public void shouldGetGenreByIdTest() {
        Assertions.assertEquals("Триллер", filmGenreDbStorage.getById(4L).get().getName());
        Assertions.assertEquals("Документальный", filmGenreDbStorage.getById(5L).get().getName());
        Assertions.assertEquals("Боевик", filmGenreDbStorage.getById(6L).get().getName());
    }

    @Test
    public void shouldGetGenreByNameTest() {
        Assertions.assertEquals("Комедия", filmGenreDbStorage.getByName("Комедия").get().getName());
        Assertions.assertEquals("Драма", filmGenreDbStorage.getByName("Драма").get().getName());
        Assertions.assertEquals("Мультфильм", filmGenreDbStorage.getByName("Мультфильм").get().getName());

    }

    @Test
    public void shouldAddGenreTest() {
        FilmGenre scifi = FilmGenre.builder()
                .id(7L)
                .name("Научная фантастика")
                .build();

        //проверка
        int genresAmountBefore = filmGenreDbStorage.getAll().size();
        FilmGenre addedGenre = filmGenreDbStorage.addGenre(scifi).get();
        Assertions.assertEquals(genresAmountBefore + 1, filmGenreDbStorage.getAll().size());
        FilmGenre gottedGenre = filmGenreDbStorage.getById(addedGenre.getId()).get();
        Assertions.assertEquals(addedGenre.getName(), gottedGenre.getName());

    }

}
