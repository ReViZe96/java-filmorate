package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FriendsManipulationException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }


    public Collection<User> getAllUsers() {
        return userStorage.getAll();
    }

    public User getUserById(Long id) {
        Optional<User> user = userStorage.getUserById(id);
        if (user.isPresent()) {
            return user.get();
        } else {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
    }

    public User createUser(User user) {
        boolean isUserValid = isUserValid(user);
        if (isUserValid) {
            log.info("Добавляемый пользователь {} валиден", user.getLogin());
            user.setId(userStorage.getNextId());
            if (user.getName() == null || user.getName().isBlank()) {
                user.setName(user.getLogin());
                log.info("У добавляемого пользователя отстутствует имя. В качестве имени будет использован логин = {}",
                        user.getLogin());
            }
            userStorage.addUser(user);
            log.info("В системе зарегистрирован новый пользователь с логином: {}", user.getLogin());
        }
        return user;
    }

    public User updateUser(User updatedUser) {
        User oldUser;
        if (updatedUser.getId() == null) {
            throw new ValidationException("Id должен быть указан");
        }
        if (userStorage.isUserExist(updatedUser.getId())) {
            oldUser = userStorage.getUserById(updatedUser.getId()).get();
            log.info("Обновляемый пользователь {} найден", updatedUser.getLogin());
            oldUser.setEmail(updatedUser.getEmail());
            oldUser.setLogin(updatedUser.getLogin());
            oldUser.setName(updatedUser.getName());
            oldUser.setBirthday(updatedUser.getBirthday());
            if (updatedUser.getFriends() == null) {
                oldUser.setFriends(new HashSet<>());
            } else {
                oldUser.setFriends(updatedUser.getFriends());
            }
            log.info("В системе обновлены данные о пользователе с логином: {}", updatedUser.getLogin());
            return oldUser;
        } else {
            throw new NotFoundException("Пользователь с id = " + updatedUser.getId() + " не найден");
        }
    }

    public User addToFriends(Long id, Long friendId) {
        if (id == null) {
            throw new ValidationException("Id пользователя, добавляющего в друзья, должен быть указан");
        }
        if (friendId == null) {
            throw new ValidationException("Id пользователя, добавляемого в друзья, должен быть указан");
        }
        if (!userStorage.isUserExist(id)) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
        if (!userStorage.isUserExist(friendId)) {
            throw new NotFoundException("Пользователь с id = " + friendId + " не найден");
        }
        Set<Long> firstUserFriendsIds = userStorage.getUserFriendsIds(id);
        Set<Long> secondUserFriendsIds = userStorage.getUserFriendsIds(friendId);
        if (firstUserFriendsIds.contains(friendId) || secondUserFriendsIds.contains(friendId)) {
            throw new FriendsManipulationException("Пользователи уже являются друзьями");
        }
        return userStorage.addFriend(id, friendId);
    }

    public void deleteFromFriends(Long id, Long friendId) {
        if (id == null) {
            throw new ValidationException("Id пользователя, расторгающего дружбу, должен быть указан");
        }
        if (friendId == null) {

            throw new ValidationException("Id пользователя, удаляемого из друзей, должен быть указан");
        }
        if (!userStorage.isUserExist(id)) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
        if (!userStorage.isUserExist(friendId)) {
            throw new NotFoundException("Пользователь с id = " + friendId + " не найден");
        }
        Set<Long> firstUserFriendsIds = userStorage.getUserFriendsIds(id);
        Set<Long> secondUserFriendsIds = userStorage.getUserFriendsIds(friendId);
        if (firstUserFriendsIds.contains(friendId)) {
            userStorage.removeFriend(id, friendId);
        }
        if (secondUserFriendsIds.contains(id)) {
            userStorage.removeFriend(friendId, id);
        }
        log.info("Пользователи с id = {} и id = {} больше не являются друзьями", id, friendId);
    }

    public Set<User> getUserFriends(Long id) {
        if (id == null) {
            throw new ValidationException("Id пользователя, должен быть указан");
        }
        if (!userStorage.isUserExist(id)) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
        Set<Long> friendsIds = userStorage.getUserFriendsIds(id);
        log.info("Список друзей пользователя с id = {}: {}", id, friendsIds);
        return friendsIds.stream().map(f -> userStorage.getUserById(f).get()).collect(Collectors.toSet());
    }

    public Set<User> getCommonFriends(Long id, Long otherId) {
        if (id == null || otherId == null) {
            throw new FriendsManipulationException("Id одного из пользователей не указан");
        }
        if (!userStorage.isUserExist(id)) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
        if (!userStorage.isUserExist(otherId)) {
            throw new NotFoundException("Пользователь с id = " + otherId + " не найден");
        }
        Set<Long> firstUserFirendIds = userStorage.getUserFriendsIds(id);
        Set<Long> secondUserFriendIds = userStorage.getUserFriendsIds(otherId);
        firstUserFirendIds.retainAll(secondUserFriendIds);
        log.info("Найдены общие друзья для пользователей с id = {} и id = {}", id, otherId);
        return firstUserFirendIds.stream().map(f -> userStorage.getUserById(f).get()).collect(Collectors.toSet());
    }


    private boolean isUserValid(User user) throws ValidationException {
        String newUsersEmail = user.getEmail();
        String newUsersLogin = user.getLogin();
        if (newUsersEmail == null || newUsersEmail.isBlank() || !newUsersEmail.contains("@")) {
            throw new ValidationException("Электронная почта создаваемого пользователя не может быть пустой " +
                    "и должна содержать символ @");
        }
        if (newUsersLogin == null || newUsersLogin.isBlank() || newUsersLogin.contains(" ")) {
            throw new ValidationException("Логин создаваемого пользователя не может быть пустым и содержать пробелы");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения создаваемого пользователя не может быть в будущем");
        }
        return true;
    }

}
