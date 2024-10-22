package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.HashSet;

public class UserControllerTest {

    InMemoryUserStorage userStorage = new InMemoryUserStorage();
    UserService userService = new UserService(userStorage);
    UserController userController = new UserController(userService);

    @Test
    public void shouldCreatedUser() {
        User correctUser = User.builder()
                .email("revize96@yandex.ru")
                .login("RVZ")
                .name("Artem")
                .birthday(LocalDate.of(1996, 5, 10))
                .friends(new HashSet<>())
                .build();

        Assertions.assertTrue(userStorage.getSavedUsers().isEmpty());
        Assertions.assertDoesNotThrow(() -> userController.createUser(correctUser));
        Assertions.assertEquals(userStorage.getSavedUsers().size(), 1);
        Assertions.assertTrue(userStorage.getSavedUsers().contains(correctUser));
    }

    @Test
    public void shouldNotCreatedUserWithNullEmptyOrNotCorrectEmail() {
        User userWithoutEmail = User.builder()
                .email(null)
                .login("firstUser")
                .name("First")
                .birthday(LocalDate.of(1991, 1, 1))
                .friends(new HashSet<>())
                .build();
        User userWithEmptyEmail = User.builder()
                .email("")
                .login("secondUser")
                .name("Second")
                .birthday(LocalDate.of(1992, 2, 2))
                .friends(new HashSet<>())
                .build();
        User userWithNotCorrectEmail = User.builder()
                .email("thirdUser.com")
                .login("@-hater")
                .name("NotEmailButWebSite")
                .birthday(LocalDate.of(1993, 3, 3))
                .friends(new HashSet<>())
                .build();

        Assertions.assertThrowsExactly(ValidationException.class, () -> userController.createUser(userWithoutEmail));
        Assertions.assertThrowsExactly(ValidationException.class, () -> userController.createUser(userWithEmptyEmail));
        Assertions.assertThrowsExactly(ValidationException.class, () -> userController.createUser(userWithNotCorrectEmail));
        Assertions.assertTrue(userStorage.getSavedUsers().isEmpty());
    }

    @Test
    public void shouldNotCreatedUserWithNullBlankOrWhitespaceContainingLogin() {
        User userWithoutLogin = User.builder()
                .email("withoutLogin@gmail.com")
                .login(null)
                .name("WithoutLogin")
                .birthday(LocalDate.of(1994, 4, 4))
                .friends(new HashSet<>())
                .build();
        User userWithEmptyLogin = User.builder()
                .email("withEmptyLogin@yahoo.com")
                .login("")
                .name("WithEmptyLogin")
                .birthday(LocalDate.of(1995, 5, 5))
                .friends(new HashSet<>())
                .build();
        User userWithWhitespaceContainedLogin = User.builder()
                .email("whitespace_contain@mail.ru")
                .login("whitespace contain")
                .name("Василий Петрович")
                .birthday(LocalDate.of(1996, 6, 6))
                .friends(new HashSet<>())
                .build();

        Assertions.assertThrowsExactly(ValidationException.class, () -> userController.createUser(userWithoutLogin));
        Assertions.assertThrowsExactly(ValidationException.class, () -> userController.createUser(userWithEmptyLogin));
        Assertions.assertThrowsExactly(ValidationException.class, () -> userController.createUser(userWithWhitespaceContainedLogin));
        Assertions.assertTrue(userStorage.getSavedUsers().isEmpty());
    }

    @Test
    public void shouldNotCreatedUserWithBirthdayInFuture() {
        User userThatNotBornYet = User.builder()
                .email("notBornedYet@haven.net")
                .login("NotBornedYet")
                .name("PreChildren")
                .birthday(LocalDate.of(2999, 12, 12))
                .friends(new HashSet<>())
                .build();

        Assertions.assertThrowsExactly(ValidationException.class, () -> userController.createUser(userThatNotBornYet));
        Assertions.assertTrue(userStorage.getSavedUsers().isEmpty());
    }

    @Test
    public void shouldCreatedUserWithNullOrBlankName() {
        User userWithoutName = User.builder()
                .email("withoutName@ya.ru")
                .login("withoutName")
                .name(null)
                .birthday(LocalDate.of(1997, 7, 7))
                .friends(new HashSet<>())
                .build();
        User userWithEmptyName = User.builder()
                .email("withEmptyName@yahoo.com")
                .login("withEmptyName")
                .name("")
                .birthday(LocalDate.of(1998, 8, 8))
                .friends(new HashSet<>())
                .build();

        Assertions.assertTrue(userStorage.getSavedUsers().isEmpty());
        Assertions.assertDoesNotThrow(() -> userController.createUser(userWithoutName));
        Assertions.assertDoesNotThrow(() -> userController.createUser(userWithEmptyName));
        Assertions.assertEquals(userStorage.getSavedUsers().size(), 2);
        Assertions.assertTrue(userStorage.getSavedUsers().contains(userWithoutName));
        Assertions.assertTrue(userStorage.getSavedUsers().contains(userWithEmptyName));
    }

    @Test
    public void shouldUpdateUser() {
        User creatingUser = User.builder()
                .email("creatingUser@rambler.com")
                .login("creatingUser")
                .name("CreatingUser")
                .birthday(LocalDate.of(1999, 9, 9))
                .friends(new HashSet<>())
                .build();

        UserDto createdUser = userController.createUser(creatingUser);

        User updatingUser = User.builder()
                .id(createdUser.getId())
                .email("updatingUser@gmail.com")
                .login("updatingUser")
                .name("UpdatingUser")
                .birthday(LocalDate.of(2000, 10, 10))
                .friends(new HashSet<>())
                .build();

        Assertions.assertEquals(userStorage.getSavedUsers().size(), 1);

        Assertions.assertDoesNotThrow(() -> userController.updateUser(updatingUser));
        Assertions.assertEquals(userStorage.getSavedUsers().size(), 1);
        Assertions.assertTrue(userStorage.getSavedUsers().contains(updatingUser));
    }

    @Test
    public void shouldNotUpdatedFilmWithoutId() {
        User userWithoutId = User.builder()
                .email("userWithoutId@ya.com")
                .login("userWithoutId")
                .name("UserWithoutId")
                .birthday(LocalDate.of(2001, 11, 11))
                .friends(new HashSet<>())
                .build();

        Assertions.assertThrowsExactly(ValidationException.class, () -> userController.updateUser(userWithoutId));
    }

}
