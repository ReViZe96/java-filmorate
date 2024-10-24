package ru.yandex.practicum.filmorate.storage.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.FilmGenre;

import java.util.List;
import java.util.Optional;

@Repository
public class FilmGenreDbStorage extends BaseDbStorage<FilmGenre> {

    private static final String FIND_ALL_GENRES_QUERY = "SELECT * FROM Genres";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM Genres WHERE id = ?";

    public FilmGenreDbStorage(JdbcTemplate jdbc, RowMapper<FilmGenre> mapper) {
        super(jdbc, mapper);
    }

    public List<FilmGenre> getAll() {
        return findMany(FIND_ALL_GENRES_QUERY);
    }

    public Optional<FilmGenre> getById(Long genreId) {
        return findOne(FIND_BY_ID_QUERY, genreId);
    }

}
