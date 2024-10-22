package ru.yandex.practicum.filmorate.storage.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.AgeRestriction;

import java.util.List;
import java.util.Optional;

@Repository
public class AgeRestrictionDbStorage extends BaseDbStorage<AgeRestriction> {

    private static final String FIND_ALL_AGE_RESTRICTIONS_QUERY = "SELECT * FROM Age_restrictions";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM Age_restrictions WHERE id = ?";

    public AgeRestrictionDbStorage(JdbcTemplate jdbc, RowMapper<AgeRestriction> mapper) {
        super(jdbc, mapper);
    }

    public List<AgeRestriction> getAll() {
        return findMany(FIND_ALL_AGE_RESTRICTIONS_QUERY);
    }

    public Optional<AgeRestriction> findById(Long id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }

}
