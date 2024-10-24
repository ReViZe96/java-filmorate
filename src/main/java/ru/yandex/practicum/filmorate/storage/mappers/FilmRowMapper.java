package ru.yandex.practicum.filmorate.storage.mappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.dal.MpaDbStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;

@Component
public class FilmRowMapper implements RowMapper<Film> {

    private MpaDbStorage mpaDbStorage;

    @Autowired
    public FilmRowMapper(MpaDbStorage mpaDbStorage) {
        this.mpaDbStorage = mpaDbStorage;
    }

    @Override
    public Film mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        LocalDate releaseDate = LocalDate.ofInstant(resultSet.getTimestamp("release_date").toInstant(),
                ZoneId.systemDefault());
        Mpa mpa = mpaDbStorage.findById(resultSet.getLong("mpa_id")).get();

        Film film = Film.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(releaseDate)
                .duration(resultSet.getLong("duration"))
                .mpa(mpa)
                .build();
        return film;
    }
}
