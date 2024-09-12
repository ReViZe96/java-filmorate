package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;

@RestController
@RequestMapping("users")
public class UserController {

    private final Logger log = LoggerFactory.getLogger(UserController.class);

    private HashMap<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getAll() {
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User newUser) {
        String newUsersEmail = newUser.getEmail();
        String newUsersLogin = newUser.getLogin();

        try {
            if (newUsersEmail == null || newUsersEmail.isBlank() || !newUsersEmail.contains("@")) {
                throw new ValidationException("Электронная почта создаваемого пользователя не может быть пустой " +
                        "и должна содержать символ @");
            }

            if (newUsersLogin == null || newUsersLogin.isBlank() || newUsersLogin.contains(" ")) {
                throw new ValidationException("Логин создаваемого пользователя не может быть пустым и содержать пробелы");
            }

            if (newUser.getBirthday().isAfter(LocalDate.now())) {
                throw new ValidationException("Дата рождения создаваемого пользователя не может быть в будущем");
            }

            newUser.setId(getNextId());

            if (newUser.getName() == null || newUser.getName().isBlank()) {
                newUser.setName(newUser.getLogin());
            }

            users.put(newUser.getId(), newUser);
            log.info("В системе зарегистрирован новый пользователь с логином: {}", newUser.getLogin());
            return newUser;

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping
    public User update(@RequestBody User updatedUser) {
        User oldUser = null;
        try {
            if (updatedUser.getId() == null) {
                throw new ValidationException("Id должен быть указан");
            }

            if (users.containsKey(updatedUser.getId())) {
                oldUser = users.get(updatedUser.getId());

                oldUser.setEmail(updatedUser.getEmail());
                oldUser.setLogin(updatedUser.getLogin());
                oldUser.setName(updatedUser.getName());
                oldUser.setBirthday(updatedUser.getBirthday());
                log.info("В системе обновлены данные о пользователе с логином: {}", updatedUser.getLogin());
                return oldUser;
            } else {
                throw new ValidationException("Пользователь с id = " + updatedUser.getId() + " не найден");
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }


    private long getNextId() {
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
