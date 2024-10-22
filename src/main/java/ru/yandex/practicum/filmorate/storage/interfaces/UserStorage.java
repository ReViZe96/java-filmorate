package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface UserStorage {

    Collection<User> getAll();

    Optional<User> getUserById(Long id);

    Optional<User> addUser(User user);

    Optional<User> updateUser(User user);

    boolean isUserExist(Long userId);

    Set<Long> getUserFriendsIds(Long id);

    Optional<User> addFriend(Long id, Long friendId);

    void removeFriend(Long id, Long friendId);
}
