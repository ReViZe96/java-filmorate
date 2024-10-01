package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {

    private final Logger log = LoggerFactory.getLogger(InMemoryUserStorage.class);

    private HashMap<Long, User> users = new HashMap<>();

    @Override
    public Collection<User> getAll() {
        return users.values();
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public void addUser(User user) {
        if (user.getFriends() == null) {
            user.setFriends(new HashSet<>());
        }
        users.put(user.getId(), user);
    }

    @Override
    public boolean isUserExist(Long userId) {
        return users.containsKey(userId);
    }

    @Override
    public Set<Long> getUserFriendsIds(Long id) {
        return users.get(id).getFriends();
    }

    @Override
    public User addFriend(Long id, Long friendId) {
        User firstUser = getUserById(id).get();
        User secondUser = getUserById(friendId).get();
        Set<Long> firstUserFriends = getUserFriendsIds(id);
        Set<Long> secondUserFriends = getUserFriendsIds(friendId);
        firstUserFriends.add(friendId);
        secondUserFriends.add(id);
        firstUser.setFriends(firstUserFriends);
        secondUser.setFriends(secondUserFriends);
        log.info("Пользователь с id = {} добавлен в друзья пользователя с id = {}", friendId, id);
        return firstUser;
    }

    @Override
    public void removeFriend(Long id, Long friendId) {
        User user = getUserById(id).get();
        Set<Long> friends = user.getFriends();
        friends.remove(friendId);
        user.setFriends(friends);
    }

    @Override
    public long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }


    //для тестов - временная мера
    public Collection<User> getSavedUsers() {
        return this.users.values();
    }

}
