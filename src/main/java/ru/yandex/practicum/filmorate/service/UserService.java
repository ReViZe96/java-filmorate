package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exception.FriendsManipulationException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mappers.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserStorage userStorage;

    @Autowired
    //userInMemoryStorage
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }


    public List<UserDto> getAllUsers() {
        return userStorage.getAll().stream().map(UserMapper::mapToUserDto).toList();
    }

    public UserDto getUserById(Long id) {
        Optional<User> user = userStorage.getUserById(id);
        if (user.isPresent()) {
            log.info("Пользователь {} найден", user.get().getLogin());
            return user.map(UserMapper::mapToUserDto).get();
        } else {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
    }

    public UserDto createUser(User newUser) {
        Optional<User> user = null;
        boolean isUserValid = isUserValid(newUser);
        if (isUserValid) {
            log.info("Добавляемый пользователь {} валиден", newUser.getLogin());
            if (newUser.getName() == null || newUser.getName().isBlank()) {
                newUser.setName(newUser.getLogin());
                log.info("У добавляемого пользователя отстутствует имя. В качестве имени будет использован логин = {}",
                        newUser.getLogin());
            }
            user = userStorage.addUser(newUser);
            log.info("В системе зарегистрирован новый пользователь с логином: {}", user.get().getLogin());
        }
        return user.map(UserMapper::mapToUserDto).get();
    }

    public UserDto updateUser(User updatedUser) {
        if (updatedUser.getId() == null) {
            throw new ValidationException("Id должен быть указан");
        }
        if (!userStorage.isUserExist(updatedUser.getId())) {
            throw new NotFoundException("Пользователь с id = " + updatedUser.getId() + " не найден");
        } else {
            Optional<User> user = userStorage.updateUser(updatedUser);
            return user.map(UserMapper::mapToUserDto).get();
        }
    }

    public UserDto addToFriends(Long id, Long friendId) {
        isUsersCanBeAddedToFriend(id, friendId);
        return userStorage.addFriend(id, friendId).map(UserMapper::mapToUserDto).get();
    }

    public void deleteFromFriends(Long id, Long friendId) {
        isUsersCanBeRemovedFromFriend(id, friendId);
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

    public Set<UserDto> getUserFriends(Long id) {
        canGetUserFriends(id);
        Set<Long> friendsIds = userStorage.getUserFriendsIds(id);
        log.info("Список друзей пользователя с id = {}: {}", id, friendsIds);
        return friendsIds.stream()
                .map(f -> userStorage.getUserById(f).get())
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toSet());
    }

    public Set<UserDto> getCommonFriends(Long id, Long otherId) {
        canGetCommonFriends(id, otherId);
        Set<Long> firstUserFirendIds = userStorage.getUserFriendsIds(id);
        Set<Long> secondUserFriendIds = userStorage.getUserFriendsIds(otherId);
        firstUserFirendIds.retainAll(secondUserFriendIds);
        log.info("Найдены общие друзья для пользователей с id = {} и id = {}", id, otherId);
        return firstUserFirendIds.stream()
                .map(f -> userStorage.getUserById(f).get())
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toSet());
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

    private boolean isUsersCanBeAddedToFriend(Long id, Long friendId) {
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
        return true;
    }

    private boolean isUsersCanBeRemovedFromFriend(Long id, Long friendId) {
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
        return true;
    }

    private boolean canGetUserFriends(Long id) {
        if (id == null) {
            throw new ValidationException("Id пользователя, должен быть указан");
        }
        if (!userStorage.isUserExist(id)) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
        return true;
    }

    private boolean canGetCommonFriends(Long id, Long otherId) {
        if (id == null || otherId == null) {
            throw new FriendsManipulationException("Id одного из пользователей не указан");
        }
        if (!userStorage.isUserExist(id)) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
        if (!userStorage.isUserExist(otherId)) {
            throw new NotFoundException("Пользователь с id = " + otherId + " не найден");
        }
        return true;
    }

}
