package ru.yandex.practicum.filmorate.storage.dal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.AgeRestriction;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Repository("filmDbStorage")
public class FilmDbStorage extends BaseDbStorage<Film> implements FilmStorage {

    private final Logger log = LoggerFactory.getLogger(FilmDbStorage.class);

    private static final String FIND_ALL_QUERY = "SELECT * FROM Films";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM Films WHERE id = ?";
    private static final String INSERT_FILM_QUERY = "INSERT INTO Films(name, description, release_date, duration, " +
            "age_restriction_id) VALUES (?, ?, ?, ?, ?, ?) returning id";
    private static final String UPDATE_FILM_QUERY = "UPDATE Films SET (name = ?, description = ?, release_date = ?, " +
            "duration = ?, age_restriction_id = ?) WHERE id = ?";

    private static final String FIND_EXIST_FILM_GENRES_RELATIONS_QUERY = "SELECT genre_id FROM Films_genres " +
            "WHERE film_id = 1 and genre_id = 2";
    private static final String INSERT_FILMS_GENRES_QUERY = "INSERT INTO Films_genres(film_id, genre_id) VALUES (?, ?)";

    private static final String FIND_LIKES_BY_FILM_ID_QUERY = "SELECT * FROM User_likes WHERE film_id = ";
    private static final String FIND_EXIST_FILM_LIKES_RELATIONS_QUERY = "SELECT user_id FROM User_likes WHERE " +
            "film_id = 1 and user_id = 2";
    private static final String INSERT_LIKE_QUERY = "INSERT INTO User_likes(film_id, user_id) VALUES (?, ?) returning id";
    private static final String DELETE_LIKE_QUERY = "DELETE * FROM User_likes where film_id = ? and user_id = ?";

    private UserDbStorage userDbStorage;
    private FilmGenreDbStorage filmGenreDbStorage;
    private AgeRestrictionDbStorage ageRestrictionDbStorage;

    public FilmDbStorage(JdbcTemplate jdbc, RowMapper<Film> mapper, UserDbStorage userDbStorage,
                         FilmGenreDbStorage filmGenreDbStorage, AgeRestrictionDbStorage ageRestrictionDbStorage) {
        super(jdbc, mapper);
        this.userDbStorage = userDbStorage;
        this.filmGenreDbStorage = filmGenreDbStorage;
        this.ageRestrictionDbStorage = ageRestrictionDbStorage;
    }

    @Override
    public List<Film> getAll() {
        List<Film> allFilms = findMany(FIND_ALL_QUERY);
        for (Film film : allFilms) {
            film.setLikes(getFilmLikeIds(film.getId()));
            film.setGenreIds(getFilmGenresIds(film.getId()));
        }
        return allFilms;
    }

    @Override
    public Optional<Film> getFilmById(Long id) {
        Film film = findOne(FIND_BY_ID_QUERY, id).get();
        film.setLikes(getFilmLikeIds(id));
        film.setGenreIds(getFilmGenresIds(id));
        return Optional.of(film);
    }

    @Override
    public Optional<Film> addFilm(Film newFilm) {
        Optional<AgeRestriction> ageRestriction = isFieldsFromAnotherTablesValid(newFilm);
        Long filmId = insert(INSERT_FILM_QUERY,
                newFilm.getName(),
                newFilm.getDescription(),
                Timestamp.from(Instant.from(newFilm.getReleaseDate())),
                newFilm.getDuration(),
                ageRestriction
        );
        for (Long userLikeId : newFilm.getLikes()) {
            insert(INSERT_LIKE_QUERY, newFilm.getId(), userLikeId);
        }
        for (Long genreId : newFilm.getGenreIds()) {
            insert(INSERT_FILMS_GENRES_QUERY, newFilm.getId(), genreId);
        }
        return findOne(FIND_BY_ID_QUERY, filmId);
    }

    @Override
    public Optional<Film> updateFilm(Film updatedFilm) {
        Optional<Film> film = findOne(FIND_BY_ID_QUERY, updatedFilm.getId());
        if (film.isPresent()) {
            log.info("Обновляемый фильм {} найден", film.get().getName());
            Optional<AgeRestriction> ageRestriction = isFieldsFromAnotherTablesValid(updatedFilm);
            update(UPDATE_FILM_QUERY,
                    updatedFilm.getName(),
                    updatedFilm.getDescription(),
                    Timestamp.from(Instant.from(updatedFilm.getReleaseDate())),
                    updatedFilm.getDuration(),
                    ageRestriction,
                    film.get().getId()
            );
            for (Long userLikeId : film.get().getLikes()) {
                if (findExistFilmLikes(film.get().getId(), userLikeId).isEmpty()) {
                    insert(INSERT_LIKE_QUERY, film.get().getId(), userLikeId);
                }
            }
            for (Long genreId : film.get().getGenreIds()) {
                if (findExistFilmGenres(film.get().getId(), genreId).isEmpty()) {
                    insert(INSERT_FILMS_GENRES_QUERY, film.get().getId(), genreId);
                }
            }
            log.info("В системе обновлены данные о фильме под названием: {}", updatedFilm.getName());
        }
        return findOne(FIND_BY_ID_QUERY, film.get().getId());
    }

    @Override
    public boolean isFilmExist(Long filmId) {
        return findOne(FIND_BY_ID_QUERY, filmId).isPresent();
    }

    @Override
    public Set<Long> getFilmLikeIds(Long id) {
        String query = FIND_LIKES_BY_FILM_ID_QUERY + id;
        return jdbc.query(query, (ResultSet rs) -> {
            Set<Long> userIds = new HashSet<>();
            while (rs.next()) {
                userIds.add(rs.getLong("user_id"));
            }
            return userIds;
        });
    }

    @Override
    public Optional<Film> addLike(Long filmId, Long userId) {
        insert(INSERT_LIKE_QUERY, filmId, userId);
        Optional<Film> film = getFilmById(filmId);
        log.info("Лайк пользователя с id = {} добавлен фильму {}", userId, film.get().getName());
        return film;
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        int rowsDeleted = jdbc.update(DELETE_LIKE_QUERY, filmId, userId);
        if (rowsDeleted > 0) {
            log.info("Лайк пользователя с id = {} удален у фильма c id = {}", userId, filmId);
        }
    }

    @Override
    public List<FilmGenre> getAllGenres() {
        return filmGenreDbStorage.getAll();
    }

    @Override
    public Optional<FilmGenre> getGenreById(Long genreId) {
        return filmGenreDbStorage.getById(genreId);
    }

    @Override
    public List<AgeRestriction> getAllAgeRestrictions() {
        return ageRestrictionDbStorage.getAll();
    }

    @Override
    public Optional<AgeRestriction> getAgeRestrictionById(Long ageRestrictionId) {
        return ageRestrictionDbStorage.findById(ageRestrictionId);
    }

    private Set<Long> getFilmGenresIds(Long filmId) {
        return filmGenreDbStorage.getByFilmId(filmId).stream().map(FilmGenre::getId).collect(Collectors.toSet());
    }

    private Optional<AgeRestriction> isFieldsFromAnotherTablesValid(Film film) {
        Optional<AgeRestriction> ageRestriction = ageRestrictionDbStorage.findById(film.getAgeRestrictionId());
        if (!ageRestriction.isPresent()) {
            throw new NotFoundException("Возрастное ограничение не найдено");
        }

        for (Long genreId : film.getGenreIds()) {
            Optional<FilmGenre> filmGenre = filmGenreDbStorage.getById(genreId);
            if (!filmGenre.isPresent()) {
                throw new NotFoundException("Жанр фильма не найден");
            }
        }

        for (Long userLikeId : film.getLikes()) {
            Optional<User> user = userDbStorage.getUserById(userLikeId);
            if (!user.isPresent()) {
                throw new NotFoundException("Пользователь с id = " + userLikeId + " не мог поставить лайк фильму, т.к." +
                        " данного пользователя не существует");
            }
        }
        return ageRestriction;
    }

    private Set<Long> findExistFilmLikes(Long filmId, Long userId) {
        String parametrizedQuery = FIND_EXIST_FILM_LIKES_RELATIONS_QUERY
                .replace("1", filmId.toString())
                .replace("2", userId.toString());
        return jdbc.query(parametrizedQuery, (ResultSet rs) -> {
            Set<Long> userIds = new HashSet<>();
            while (rs.next()) {
                userIds.add(rs.getLong("user_id"));
            }
            return userIds;
        });
    }

    private Set<Long> findExistFilmGenres(Long filmId, Long genreId) {
        String parametrizedQuery = FIND_EXIST_FILM_GENRES_RELATIONS_QUERY
                .replace("1", filmId.toString())
                .replace("2", genreId.toString());
        return jdbc.query(parametrizedQuery, (ResultSet rs) -> {
            Set<Long> genreIds = new HashSet<>();
            while (rs.next()) {
                genreIds.add(rs.getLong("genre_id"));
            }
            return genreIds;
        });
    }

}
