package ru.yandex.practicum.filmorate.storage.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.FilmGenre;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FilmGenreRowMapper implements RowMapper<FilmGenre> {
    @Override
    public FilmGenre mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return FilmGenre.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .build();

        //для расширенной модели
        //return FilmGenre.builder()
        //      .id(resultSet.getLong("id"))
        //      .englishName(resultSet.getString("english_name"))
        //      .russianName(resultSet.getString("russian_name"))
        //      .build();
    }
}
