package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dal.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.dal.FilmGenreDbStorage;
import ru.yandex.practicum.filmorate.storage.dal.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.dal.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.mappers.FilmGenreRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.MpaRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;

import java.time.LocalDate;
import java.util.List;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({
        FilmDbStorage.class, FilmRowMapper.class,
        UserDbStorage.class, UserRowMapper.class,
        FilmGenreDbStorage.class, FilmGenreRowMapper.class,
        MpaDbStorage.class, MpaRowMapper.class
})
public class FilmDbStorageTest {

    private final FilmDbStorage filmDbStorage;
    private final UserDbStorage userDbStorage;
    private final FilmGenreDbStorage filmGenreDbStorage;
    private final MpaDbStorage mpaDbStorage;


    @Test
    public void shouldGetAllFilmsTest() {
        FilmGenre comedy = filmGenreDbStorage.getById(1L).get();
        FilmGenre drama = filmGenreDbStorage.getById(2L).get();
        FilmGenre cartoon = filmGenreDbStorage.getById(3L).get();

        Mpa g = mpaDbStorage.findById(1L).get();
        Mpa pg = mpaDbStorage.findById(2L).get();
        Mpa pgThirteen = mpaDbStorage.findById(3L).get();

        Film firstFilm = Film.builder()
                .name("Movie 1")
                .description("Первый фильм")
                .releaseDate(LocalDate.of(2000, 01, 01))
                .duration(7000L)
                .mpa(pgThirteen)
                .genres(List.of(drama))
                .build();
        Film secondFilm = Film.builder()
                .name("Movie 2")
                .description("Второй фильм")
                .releaseDate(LocalDate.of(2000, 02, 02))
                .duration(7200L)
                .mpa(pg)
                .genres(List.of(drama, comedy))
                .build();
        Film thirdFilm = Film.builder()
                .name("Movie 1")
                .description("Третий, но мультфильм")
                .releaseDate(LocalDate.of(2000, 03, 03))
                .duration(6400L)
                .mpa(g)
                .genres(List.of(cartoon))
                .build();

        //проверка
        Assertions.assertTrue(filmDbStorage.getAll().isEmpty());
        filmDbStorage.addFilm(firstFilm);
        filmDbStorage.addFilm(secondFilm);
        filmDbStorage.addFilm(thirdFilm);
        Assertions.assertTrue(filmDbStorage.getAll().size() == 3);

    }

    @Test
    public void shouldGetFilmByIdTest() {
        FilmGenre drama = filmGenreDbStorage.getById(2L).get();
        Mpa pgThirteen = mpaDbStorage.findById(3L).get();

        Film film = Film.builder()
                .name("Movie")
                .description("Кино")
                .releaseDate(LocalDate.of(2024, 05, 10))
                .duration(5000L)
                .mpa(pgThirteen)
                .genres(List.of(drama))
                .build();

        Film newFilm = filmDbStorage.addFilm(film).get();
        Film gottedFilm = filmDbStorage.getFilmById(newFilm.getId()).get();

        //проверка
        Assertions.assertEquals(newFilm.getName(), gottedFilm.getName());
        Assertions.assertEquals(newFilm.getDescription(), gottedFilm.getDescription());
        Assertions.assertEquals(newFilm.getReleaseDate(), gottedFilm.getReleaseDate());
        Assertions.assertEquals(newFilm.getDuration(), gottedFilm.getDuration());
        Assertions.assertEquals(newFilm.getMpa(), gottedFilm.getMpa());
        Assertions.assertEquals(newFilm.getGenres(), gottedFilm.getGenres());

    }

    @Test
    public void shouldAddFilmTest() {
        FilmGenre cartoon = filmGenreDbStorage.getById(3L).get();
        Mpa g = mpaDbStorage.findById(1L).get();

        Film multfilm = Film.builder()
                .name("Мультфильм")
                .description("Для самых маленьких")
                .releaseDate(LocalDate.of(2020, 10, 1))
                .duration(4000L)
                .mpa(g)
                .genres(List.of(cartoon))
                .build();

        //проверка
        int filmsAmountBefore = filmDbStorage.getAll().size();
        Film addedMultfilm = filmDbStorage.addFilm(multfilm).get();
        Assertions.assertEquals(filmsAmountBefore + 1, filmDbStorage.getAll().size());
        Film gottedMultfilm = filmDbStorage.getFilmById(addedMultfilm.getId()).get();
        Assertions.assertEquals(addedMultfilm.getName(), gottedMultfilm.getName());
        Assertions.assertEquals(addedMultfilm.getDescription(), gottedMultfilm.getDescription());
        Assertions.assertEquals(addedMultfilm.getReleaseDate(), gottedMultfilm.getReleaseDate());
        Assertions.assertEquals(addedMultfilm.getDuration(), gottedMultfilm.getDuration());
        Assertions.assertEquals(addedMultfilm.getMpa(), gottedMultfilm.getMpa());
        Assertions.assertEquals(addedMultfilm.getGenres(), gottedMultfilm.getGenres());

    }

    @Test
    public void shouldUpdateFilmTest() {
        FilmGenre drama = filmGenreDbStorage.getById(2L).get();

        Mpa g = mpaDbStorage.findById(1L).get();
        Mpa pg = mpaDbStorage.findById(2L).get();

        Film film = Film.builder()
                .name("Обновляемый фильм")
                .description("Фильм до обновления")
                .releaseDate(LocalDate.of(1999, 12, 31))
                .duration(7600L)
                .mpa(pg)
                .genres(List.of(drama))
                .build();
        Film beforeUpdateFilm = filmDbStorage.addFilm(film).get();

        Film updatedFilm = Film.builder()
                .id(beforeUpdateFilm.getId())
                .name("Обновленный фильм")
                .description("Фильм после обновления")
                .releaseDate(LocalDate.of(2000, 11, 30))
                .duration(4600L)
                .mpa(g)
                .genres(List.of(drama))
                .build();

        //проверка
        filmDbStorage.updateFilm(updatedFilm);
        Film afterUpdateFilm = filmDbStorage.getFilmById(beforeUpdateFilm.getId()).get();
        Assertions.assertEquals("Обновленный фильм", afterUpdateFilm.getName());
        Assertions.assertEquals("Фильм после обновления", afterUpdateFilm.getDescription());
        Assertions.assertEquals(LocalDate.of(2000, 11, 30), afterUpdateFilm.getReleaseDate());
        Assertions.assertEquals(4600L, afterUpdateFilm.getDuration());
        Assertions.assertEquals(g, afterUpdateFilm.getMpa());
        Assertions.assertEquals(afterUpdateFilm.getGenres().get(0), drama);

    }

    @Test
    public void isFilmExistTest() {
        FilmGenre cartoon = filmGenreDbStorage.getById(3L).get();
        Mpa pgThirteen = mpaDbStorage.findById(3L).get();

        Film existCartoon = Film.builder()
                .name("Мультик")
                .description("Для подростков")
                .releaseDate(LocalDate.of(2002, 02, 13))
                .duration(6600L)
                .mpa(pgThirteen)
                .genres(List.of(cartoon))
                .build();
        Film notExistCartoon = Film.builder()
                .id(111L)
                .name("Мультипликационный фильм")
                .description("Для детей постарше")
                .releaseDate(LocalDate.of(2003, 03, 14))
                .duration(8600L)
                .mpa(pgThirteen)
                .genres(List.of(cartoon))
                .build();

        Film existed = filmDbStorage.addFilm(existCartoon).get();

        //проверка
        Assertions.assertTrue(filmDbStorage.isFilmExist(existed.getId()));
        Assertions.assertFalse(filmDbStorage.isFilmExist(notExistCartoon.getId()));

    }

    @Test
    public void shouldMakeManipulationsWithLikesTest() {
        FilmGenre comedy = filmGenreDbStorage.getById(1L).get();
        Mpa g = mpaDbStorage.findById(1L).get();
        Film film = Film.builder()
                .name("Популярный фильм")
                .description("Фильм, который понравится")
                .releaseDate(LocalDate.of(2024, 9, 9))
                .duration(4700L)
                .mpa(g)
                .genres(List.of(comedy))
                .build();
        User user = User.builder()
                .email("randomUser@gmail.com")
                .login("random")
                .name("Рандом Рандомович Рандомов")
                .birthday(LocalDate.of(1986, 1, 8))
                .build();

        Film popularFilm = filmDbStorage.addFilm(film).get();
        User likedUser = userDbStorage.addUser(user).get();

        //проверки
        Assertions.assertNull(popularFilm.getLikes());
        filmDbStorage.addLike(popularFilm.getId(), likedUser);
        List<User> likedUsersFirst = filmDbStorage.getFilmLikeIds(popularFilm.getId());
        Assertions.assertEquals(likedUsersFirst.size(), 1);
        Assertions.assertEquals(likedUsersFirst.get(0).getId(), likedUser.getId());
        Assertions.assertEquals(likedUsersFirst.get(0).getEmail(), likedUser.getEmail());
        Assertions.assertEquals(likedUsersFirst.get(0).getLogin(), likedUser.getLogin());
        Assertions.assertEquals(likedUsersFirst.get(0).getName(), likedUser.getName());
        filmDbStorage.removeLike(popularFilm.getId(), likedUser);
        List<User> likedUsersSecond = filmDbStorage.getFilmLikeIds(popularFilm.getId());
        Assertions.assertEquals(likedUsersSecond.size(), 0);

    }

}
