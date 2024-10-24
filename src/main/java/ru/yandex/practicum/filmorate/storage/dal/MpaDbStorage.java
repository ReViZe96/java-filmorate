package ru.yandex.practicum.filmorate.storage.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

@Repository
public class MpaDbStorage extends BaseDbStorage<Mpa> {

    private static final String FIND_ALL_MOTION_PICTURE_ASSOCIATIONS_QUERY = "SELECT * FROM Motion_Picture_Associations";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM Motion_Picture_Associations WHERE id = ?";

    public MpaDbStorage(JdbcTemplate jdbc, RowMapper<Mpa> mapper) {
        super(jdbc, mapper);
    }

    public List<Mpa> getAll() {
        return findMany(FIND_ALL_MOTION_PICTURE_ASSOCIATIONS_QUERY);
    }

    public Optional<Mpa> findById(Long id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }

}
