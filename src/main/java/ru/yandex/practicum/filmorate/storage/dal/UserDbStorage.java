package ru.yandex.practicum.filmorate.storage.dal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository("userDbStorage")
public class UserDbStorage extends BaseDbStorage<User> implements UserStorage {

    private final Logger log = LoggerFactory.getLogger(UserDbStorage.class);

    private static final String FIND_ALL_QUERY = "SELECT * FROM Users";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM Users where id = ?";
    private static final String INSERT_USER_QUERY = "INSERT INTO Users(email, login, name, birthday)" +
            "VALUES (?, ?, ?, ?)";
    private static String UPDATE_USER_QUERY = "UPDATE Users SET email = ?, login = ?, name = ?, birthday = ? " +
            "where id = ?";

    private static final String FIND_FRIENDS_BY_ID = "SELECT id FROM Users where id IN (" +
            "SELECT subscribing_friend_id FROM Friend_relationship " +
            "WHERE accepting_friend_id = 1)";
    private static final String FIND_EXIST_FRIENDSHIP_RELATIONS_QUERY = "SELECT subscribing_friend_id FROM Friend_relationship " +
            "WHERE accepting_friend_id = 1 and subscribing_friend_id = 2";
    private static final String INSERT_FRIEND_RELATIONSHIP_QUERY = "INSERT INTO Friend_relationship (accepting_friend_id, " +
            "subscribing_friend_id) VALUES (?, ?)";
    private static final String DELETE_FRIEND_RELATIONSHIP_QUERY = "DELETE FROM Friend_relationship " +
            "where accepting_friend_id = ? and subscribing_friend_id = ?";

    public UserDbStorage(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<User> getAll() {
        List<User> allUser = findMany(FIND_ALL_QUERY);
        for (User user : allUser) {
            user.setFriends(getUserFriendsIds(user.getId()));
        }
        return allUser;
    }

    @Override
    public Optional<User> getUserById(Long id) {
        User user = findOne(FIND_BY_ID_QUERY, id).get();
        user.setFriends(getUserFriendsIds(id));
        return Optional.of(user);
    }

    @Override
    public Optional<User> addUser(User newUser) {
        Long userId = insert(INSERT_USER_QUERY,
                newUser.getEmail(),
                newUser.getLogin(),
                newUser.getName(),
                Timestamp.from(newUser.getBirthday().atStartOfDay(ZoneId.systemDefault()).toInstant())
        );
        if (newUser.getFriends() != null) {
            isFriendsSetValid(newUser);
            for (Long userFriendId : newUser.getFriends()) {
                insert(INSERT_FRIEND_RELATIONSHIP_QUERY, newUser.getId(), userFriendId);
            }
        }
        return findOne(FIND_BY_ID_QUERY, userId);
    }

    @Override
    public Optional<User> updateUser(User updatedUser) {
        Optional<User> user = findOne(FIND_BY_ID_QUERY, updatedUser.getId());
        if (user.isPresent()) {
            log.info("Обновляемый пользователь {} найден", updatedUser.getLogin());
            update(UPDATE_USER_QUERY,
                    updatedUser.getEmail(),
                    updatedUser.getLogin(),
                    updatedUser.getName(),
                    Timestamp.from(updatedUser.getBirthday().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                    user.get().getId());
            if (updatedUser.getFriends() != null) {
                isFriendsSetValid(updatedUser);
                for (Long userFriendId : user.get().getFriends()) {
                    if (findExistFriendship(user.get().getId(), userFriendId).isEmpty()) {
                        insert(INSERT_FRIEND_RELATIONSHIP_QUERY, user.get().getId(), userFriendId);
                    }
                }
            }
            log.info("В системе обновлены данные о пользователе с логином: {}", updatedUser.getLogin());
        }
        return findOne(FIND_BY_ID_QUERY, user.get().getId());
    }

    @Override
    public boolean isUserExist(Long userId) {
        return findOne(FIND_BY_ID_QUERY, userId).isPresent();
    }

    @Override
    public Set<Long> getUserFriendsIds(Long id) {
        String parametrizedQuery = FIND_FRIENDS_BY_ID.replace("1", id.toString());
        return jdbc.query(parametrizedQuery, (ResultSet rs) -> {
            Set<Long> friendsIds = new HashSet<>();
            while (rs.next()) {
                friendsIds.add(rs.getLong("id"));
            }
            return friendsIds;
        });
    }

    @Override
    public Optional<User> addFriend(Long id, Long friendId) {
        insert(INSERT_FRIEND_RELATIONSHIP_QUERY, id, friendId);
        log.info("Пользователь с id = {} добавлен в друзья пользователя с id = {}", friendId, id);
        return getUserById(id);
    }

    @Override
    public void removeFriend(Long id, Long friendId) {
        int rowsDeleted = jdbc.update(DELETE_FRIEND_RELATIONSHIP_QUERY, id, friendId);
        if (rowsDeleted > 0) {
            log.info("Пользователи с id = {} и {} больше не являются друзьями", id, friendId);
        }
    }


    private void isFriendsSetValid(User user) {
        for (Long userFriendId : user.getFriends()) {
            Optional<User> userFriend = getUserById(userFriendId);
            if (!userFriend.isPresent()) {
                throw new NotFoundException("Пользователь с id = " + userFriendId + " не может быть в списке друзей, т.к." +
                        " данного пользователя не существует");
            }
        }
    }

    private Set<Long> findExistFriendship(Long id, Long friendId) {
        String parametrizedQuery = FIND_EXIST_FRIENDSHIP_RELATIONS_QUERY
                .replace("1", id.toString())
                .replace("2", friendId.toString());
        return jdbc.query(parametrizedQuery, (ResultSet rs) -> {
            Set<Long> friendIds = new HashSet<>();
            while (rs.next()) {
                friendIds.add(rs.getLong("subscribing_friend_id"));
            }
            return friendIds;
        });
    }

}
