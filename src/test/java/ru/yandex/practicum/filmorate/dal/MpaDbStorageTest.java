package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.dal.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.mappers.MpaRowMapper;

import java.util.List;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({
        MpaDbStorage.class,
        MpaRowMapper.class,
})
public class MpaDbStorageTest {

    private final MpaDbStorage mpaDbStorage;

    @Test
    public void shouldGetAllMpasTest() {
        List<Mpa> allMpas = mpaDbStorage.getAll();
        Assertions.assertTrue(allMpas.size() == 5);
    }

    @Test
    public void shouldFindMpaByIdTest() {
        Assertions.assertEquals("PG-13", mpaDbStorage.findById(3L).get().getName());
        Assertions.assertEquals("R", mpaDbStorage.findById(4L).get().getName());
        Assertions.assertEquals("NC-17", mpaDbStorage.findById(5L).get().getName());
    }

}
