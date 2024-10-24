package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dal.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.dal.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.dal.FilmGenreDbStorage;
import ru.yandex.practicum.filmorate.storage.dal.UserDbStorage;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTests {

    //@Qualifier("userDbStorage")
    //@Qualifier("filmDbStorage")
    private final UserDbStorage userStorage;
    private final FilmDbStorage filmDbStorage;
    private final FilmGenreDbStorage filmGenreDbStorage;
    private final MpaDbStorage mpaDbStorage;


//	@Test
//	public void testFindUserById() {
//
//		Optional<User> userOptional = userStorage.getUserById(1L);
//
//		assertThat(userOptional)
//				.isPresent()
//				.hasValueSatisfying(user ->
//						assertThat(user).hasFieldOrPropertyWithValue("id", 1)
//				);
//	}
}
