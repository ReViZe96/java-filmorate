package ru.yandex.practicum.filmorate.storage.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.AgeRestriction;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class AgeRestrictionRowMapper implements RowMapper<AgeRestriction> {
    @Override
    public AgeRestriction mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return AgeRestriction.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .build();
    }
}
