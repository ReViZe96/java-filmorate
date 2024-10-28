package ru.yandex.practicum.filmorate.storage.dal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.IncorrectGenreOrMpa;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.util.*;

@Repository("filmDbStorage")
public class FilmDbStorage extends BaseDbStorage<Film> implements FilmStorage {

    private final Logger log = LoggerFactory.getLogger(FilmDbStorage.class);

    private static final String FIND_ALL_QUERY = "SELECT * FROM Films";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM Films WHERE id = ?";
    private static final String INSERT_FILM_QUERY = "INSERT INTO Films(name, description, release_date, duration, " +
            "mpa_id) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_FILM_QUERY = "UPDATE Films SET name = ?, description = ?, release_date = ?, " +
            "duration = ?, mpa_id = ? WHERE id = ?";

    private static final String FIND_EXIST_FILM_GENRES_RELATIONS_QUERY = "SELECT genre_id FROM Films_genres " +
            "WHERE film_id = 1 and genre_id = 2";
    private static final String FIND_GENRES_IDS_BY_FILM_ID_QUERY = "SELECT * FROM Films_genres where film_id = ";
    private static final String INSERT_FILMS_GENRES_QUERY = "INSERT INTO Films_genres(film_id, genre_id) VALUES (?, ?)";

    private static final String FIND_LIKES_BY_FILM_ID_QUERY = "SELECT * FROM User_likes WHERE film_id = ";
    private static final String FIND_EXIST_FILM_LIKES_RELATIONS_QUERY = "SELECT user_id FROM User_likes WHERE " +
            "film_id = 1 and user_id = 2";
    private static final String INSERT_LIKE_QUERY = "INSERT INTO User_likes(film_id, user_id) VALUES (?, ?)";
    private static final String DELETE_LIKE_QUERY = "DELETE FROM User_likes where film_id = ? and user_id = ?";

    private UserDbStorage userDbStorage;
    private FilmGenreDbStorage filmGenreDbStorage;
    private MpaDbStorage mpaDbStorage;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbc, RowMapper<Film> mapper, UserDbStorage userDbStorage,
                         FilmGenreDbStorage filmGenreDbStorage, MpaDbStorage mpaDbStorage) {
        super(jdbc, mapper);
        this.userDbStorage = userDbStorage;
        this.filmGenreDbStorage = filmGenreDbStorage;
        this.mpaDbStorage = mpaDbStorage;
    }


    @Override
    public List<Film> getAll() {
        List<Film> allFilms = findMany(FIND_ALL_QUERY);
        for (Film film : allFilms) {
            film.setLikes(getFilmLikeIds(film.getId()));
            film.setGenres(getFilmGenres(film.getId()));
        }
        return allFilms;
    }

    @Override
    public Optional<Film> getFilmById(Long id) {
        Film film = findOne(FIND_BY_ID_QUERY, id).get();
        film.setLikes(getFilmLikeIds(id));
        film.setGenres(getFilmGenres(id));
        return Optional.of(film);
    }

    @Override
    public Optional<Film> addFilm(Film newFilm) {
        Optional<Mpa> mpa = isFieldsFromAnotherTablesValid(newFilm);

        Long filmId = insert(INSERT_FILM_QUERY,
                newFilm.getName(),
                newFilm.getDescription(),
                Timestamp.from(newFilm.getReleaseDate().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                newFilm.getDuration(),
                mpa.map(Mpa::getId).orElse(null)
        );
        if (newFilm.getLikes() != null && !newFilm.getLikes().isEmpty()) {
            for (User userLiked : newFilm.getLikes()) {
                insert(INSERT_LIKE_QUERY, newFilm.getId(), userLiked.getId());
            }
        }
        if (newFilm.getGenres() != null && !newFilm.getGenres().isEmpty()) {
            for (FilmGenre genre : newFilm.getGenres()) {
                if (filmGenreDbStorage.getById(genre.getId()).isEmpty()) {
                    if (genre.getName() != null && !genre.getName().isBlank()
                            && filmGenreDbStorage.getByName(genre.getName()).isEmpty()) {
                        filmGenreDbStorage.addGenre(genre);
                    }
                } else {
                    insert(INSERT_FILMS_GENRES_QUERY, filmId, genre.getId());
                }
            }
        }
        newFilm.setId(filmId);
        newFilm.setMpa(mpa.get());
        return Optional.of(newFilm);
    }

    @Override
    public Optional<Film> updateFilm(Film updatedFilm) {
        Optional<Film> film = findOne(FIND_BY_ID_QUERY, updatedFilm.getId());
        if (film.isPresent()) {
            log.info("Обновляемый фильм {} найден", film.get().getName());
            Optional<Mpa> mpa = isFieldsFromAnotherTablesValid(updatedFilm);
            update(UPDATE_FILM_QUERY,
                    updatedFilm.getName(),
                    updatedFilm.getDescription(),
                    Timestamp.from(updatedFilm.getReleaseDate().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                    updatedFilm.getDuration(),
                    mpa.map(Mpa::getId).orElse(null),
                    film.get().getId()
            );
            if (film.get().getLikes() != null && !film.get().getLikes().isEmpty()) {
                for (User userLiked : film.get().getLikes()) {
                    if (findExistFilmLikes(film.get().getId(), userLiked.getId()).isEmpty()) {
                        insert(INSERT_LIKE_QUERY, film.get().getId(), userLiked.getId());
                    }
                }
            }
            if (film.get().getGenres() != null && !film.get().getGenres().isEmpty()) {
                for (FilmGenre genre : film.get().getGenres()) {
                    if (findExistFilmGenres(film.get().getId(), genre.getId()).isEmpty()) {
                        insert(INSERT_FILMS_GENRES_QUERY, film.get().getId(), genre.getId());
                    }
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
    public List<User> getFilmLikeIds(Long id) {
        List<User> userLiked = new ArrayList<>();
        String query = FIND_LIKES_BY_FILM_ID_QUERY + id;
        Set<Long> userIds = new HashSet<>();
        jdbc.query(query, (ResultSet rs) -> {
            while (rs.next()) {
                userIds.add(rs.getLong("user_id"));
            }
            return userIds;
        });
        for (Long userId : userIds) {
            userLiked.add(userDbStorage.getUserById(userId).get());
        }
        return userLiked;
    }

    @Override
    public Optional<Film> addLike(Long filmId, User user) {
        insert(INSERT_LIKE_QUERY, filmId, user.getId());
        Optional<Film> film = getFilmById(filmId);
        log.info("Лайк пользователя {} добавлен фильму {}", user.getLogin(), film.get().getName());
        return film;
    }

    @Override
    public void removeLike(Long filmId, User user) {
        int rowsDeleted = jdbc.update(DELETE_LIKE_QUERY, filmId, user.getId());
        if (rowsDeleted > 0) {
            log.info("Лайк пользователя с id = {} удален у фильма c id = {}", user.getId(), filmId);
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
    public List<Mpa> getAllMpas() {
        return mpaDbStorage.getAll();
    }

    @Override
    public Optional<Mpa> getMpaById(Long mpaId) {
        return mpaDbStorage.findById(mpaId);
    }


    private List<FilmGenre> getFilmGenres(Long filmId) {
        List<FilmGenre> filmGenres = new ArrayList<>();
        String query = FIND_GENRES_IDS_BY_FILM_ID_QUERY + filmId;
        Set<Long> genreIds = new HashSet<>();
        jdbc.query(query, (ResultSet rs) -> {
            while (rs.next()) {
                genreIds.add(rs.getLong("genre_id"));
            }
            return genreIds;
        });
        for (Long genreId : genreIds) {
            filmGenres.add(filmGenreDbStorage.getById(genreId).get());
        }
        return filmGenres;
    }

    private Optional<Mpa> isFieldsFromAnotherTablesValid(Film film) {
        Optional<Mpa> mpa;
        if (film.getMpa() == null) {
            throw new IncorrectGenreOrMpa("Возрастное ограничение не указано");
        } else {
            mpa = mpaDbStorage.findById(film.getMpa().getId());
            if (mpa.isEmpty()) {
                throw new IncorrectGenreOrMpa("Возрастное ограничение не найдено");
            }
            if (film.getGenres() != null && !film.getGenres().isEmpty()) {
                for (FilmGenre genre : film.getGenres()) {
                    Optional<FilmGenre> filmGenre = filmGenreDbStorage.getById(genre.getId());
                    if (filmGenre.isEmpty()) {
                        throw new IncorrectGenreOrMpa("Жанр фильма не найден");
                    }
                }
            }
            if (film.getLikes() != null && !film.getLikes().isEmpty()) {
                for (User userLiked : film.getLikes()) {
                    Optional<User> user = userDbStorage.getUserById(userLiked.getId());
                    if (user.isEmpty()) {
                        throw new NotFoundException("Пользователь " + userLiked.getLogin() + " не мог поставить лайк фильму, т.к." +
                                " данного пользователя не существует");
                    }
                }
            }
        }
        return mpa;
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
