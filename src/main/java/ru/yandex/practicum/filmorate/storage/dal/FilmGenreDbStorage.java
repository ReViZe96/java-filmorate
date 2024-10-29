package ru.yandex.practicum.filmorate.storage.dal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.FilmGenre;

import java.util.List;
import java.util.Optional;

@Repository
public class FilmGenreDbStorage extends BaseDbStorage<FilmGenre> {

    private static final String FIND_ALL_QUERY = "SELECT * FROM Genres";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM Genres WHERE id = ?";
    private static final String FIND_BY_NAME_QUERY = "SELECT * FROM Genres WHERE name = ?";
    private static final String INSERT_GENRE_QUERY = "INSERT INTO Genres(id, name) VALUES (?, ?)";

    @Autowired
    public FilmGenreDbStorage(JdbcTemplate jdbc, RowMapper<FilmGenre> mapper) {
        super(jdbc, mapper);
    }


    public List<FilmGenre> getAll() {
        return findMany(FIND_ALL_QUERY);
    }

    public Optional<FilmGenre> getById(Long genreId) {
        return findOne(FIND_BY_ID_QUERY, genreId);
    }

    public Optional<FilmGenre> getByName(String name) {
        return findOne(FIND_BY_NAME_QUERY, name);
    }

    public Optional<FilmGenre> addGenre(FilmGenre newGenre) {
        Long genreId = insertWithKnownId(INSERT_GENRE_QUERY,
                newGenre.getId(),
                newGenre.getName());
        newGenre.setId(genreId);
        return Optional.of(newGenre);
    }

}
