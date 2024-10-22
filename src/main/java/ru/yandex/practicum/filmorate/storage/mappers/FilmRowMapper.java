package ru.yandex.practicum.filmorate.storage.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;

@Component
public class FilmRowMapper implements RowMapper<Film> {
    @Override
    public Film mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        LocalDate releaseDate = LocalDate.ofInstant(resultSet.getTimestamp("release_date").toInstant(),
                ZoneId.systemDefault());
        Film film = Film.builder()
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(releaseDate)
                .duration(resultSet.getLong("duration"))
                .ageRestrictionId(resultSet.getLong("age_restriction_id"))
                .build();
        return film;
    }
}
