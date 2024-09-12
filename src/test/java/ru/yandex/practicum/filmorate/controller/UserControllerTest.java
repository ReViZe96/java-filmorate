package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

public class UserControllerTest {

    UserController userController = new UserController();

    @Test
    public void shouldCreatedUser() {
        User correctUser = User.builder()
                .email("revize96@yandex.ru")
                .login("RVZ")
                .name("Artem")
                .birthday(LocalDate.of(1996, 5, 10))
                .build();

        Assertions.assertTrue(userController.getSavedUsers().isEmpty());
        Assertions.assertDoesNotThrow(() -> userController.create(correctUser));
        Assertions.assertEquals(userController.getSavedUsers().size(), 1);
        Assertions.assertTrue(userController.getSavedUsers().contains(correctUser));
    }

    @Test
    public void shouldNotCreatedUserWithNullEmptyOrNotCorrectEmail() {
        User userWithoutEmail = User.builder()
                .email(null)
                .login("firstUser")
                .name("First")
                .birthday(LocalDate.of(1991, 1, 1))
                .build();
        User userWithEmptyEmail = User.builder()
                .email("")
                .login("secondUser")
                .name("Second")
                .birthday(LocalDate.of(1992, 2, 2))
                .build();
        User userWithNotCorrectEmail = User.builder()
                .email("thirdUser.com")
                .login("@-hater")
                .name("NotEmailButWebSite")
                .birthday(LocalDate.of(1993, 3, 3))
                .build();

        Assertions.assertThrowsExactly(ResponseStatusException.class, () -> userController.create(userWithoutEmail));
        Assertions.assertThrowsExactly(ResponseStatusException.class, () -> userController.create(userWithEmptyEmail));
        Assertions.assertThrowsExactly(ResponseStatusException.class, () -> userController.create(userWithNotCorrectEmail));
        Assertions.assertTrue(userController.getSavedUsers().isEmpty());
    }

    @Test
    public void shouldNotCreatedUserWithNullBlankOrWhitespaceContainingLogin() {
        User userWithoutLogin = User.builder()
                .email("withoutLogin@gmail.com")
                .login(null)
                .name("WithoutLogin")
                .birthday(LocalDate.of(1994, 4, 4))
                .build();
        User userWithEmptyLogin = User.builder()
                .email("withEmptyLogin@yahoo.com")
                .login("")
                .name("WithEmptyLogin")
                .birthday(LocalDate.of(1995, 5, 5))
                .build();
        User userWithWhitespaceContainedLogin = User.builder()
                .email("whitespace_contain@mail.ru")
                .login("whitespace contain")
                .name("Василий Петрович")
                .birthday(LocalDate.of(1996, 6, 6))
                .build();

        Assertions.assertThrowsExactly(ResponseStatusException.class, () -> userController.create(userWithoutLogin));
        Assertions.assertThrowsExactly(ResponseStatusException.class, () -> userController.create(userWithEmptyLogin));
        Assertions.assertThrowsExactly(ResponseStatusException.class, () -> userController.create(userWithWhitespaceContainedLogin));
        Assertions.assertTrue(userController.getSavedUsers().isEmpty());
    }

    @Test
    public void shouldNotCreatedUserWithBirthdayInFuture() {
        User userThatNotBornYet = User.builder()
                .email("notBornedYet@haven.net")
                .login("NotBornedYet")
                .name("PreChildren")
                .birthday(LocalDate.of(2999, 12, 12))
                .build();

        Assertions.assertThrowsExactly(ResponseStatusException.class, () -> userController.create(userThatNotBornYet));
        Assertions.assertTrue(userController.getSavedUsers().isEmpty());
    }

    @Test
    public void shouldCreatedUserWithNullOrBlankName() {
        User userWithoutName = User.builder()
                .email("withoutName@ya.ru")
                .login("withoutName")
                .name(null)
                .birthday(LocalDate.of(1997, 7, 7))
                .build();
        User userWithEmptyName = User.builder()
                .email("withEmptyName@yahoo.com")
                .login("withEmptyName")
                .name("")
                .birthday(LocalDate.of(1998, 8, 8))
                .build();

        Assertions.assertTrue(userController.getSavedUsers().isEmpty());
        Assertions.assertDoesNotThrow(() -> userController.create(userWithoutName));
        Assertions.assertDoesNotThrow(() -> userController.create(userWithEmptyName));
        Assertions.assertEquals(userController.getSavedUsers().size(), 2);
        Assertions.assertTrue(userController.getSavedUsers().contains(userWithoutName));
        Assertions.assertTrue(userController.getSavedUsers().contains(userWithEmptyName));
    }

    @Test
    public void shouldUpdateUser() {
        User creatingUser = User.builder()
                .email("creatingUser@rambler.com")
                .login("creatingUser")
                .name("CreatingUser")
                .birthday(LocalDate.of(1999, 9, 9))
                .build();

        User createdUser = userController.create(creatingUser);

        User updatingUser = User.builder()
                .id(createdUser.getId())
                .email("updatingUser@gmail.com")
                .login("updatingUser")
                .name("UpdatingUser")
                .birthday(LocalDate.of(2000, 10, 10))
                .build();

        Assertions.assertEquals(userController.getSavedUsers().size(), 1);
        Assertions.assertTrue(userController.getSavedUsers().contains(createdUser));

        Assertions.assertDoesNotThrow(() -> userController.update(updatingUser));
        Assertions.assertEquals(userController.getSavedUsers().size(), 1);
        Assertions.assertTrue(userController.getSavedUsers().contains(updatingUser));
    }

    @Test
    public void shouldNotUpdatedFilmWithoutId() {
        User userWithoutId = User.builder()
                .email("userWithoutId@ya.com")
                .login("userWithoutId")
                .name("UserWithoutId")
                .birthday(LocalDate.of(2001, 11, 11))
                .build();

        Assertions.assertThrowsExactly(ResponseStatusException.class, () -> userController.update(userWithoutId));
    }

}
