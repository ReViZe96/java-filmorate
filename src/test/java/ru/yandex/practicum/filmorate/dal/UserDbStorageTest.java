package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dal.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;

import java.time.LocalDate;
import java.util.Set;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({
        UserDbStorage.class,
        UserRowMapper.class,
})
public class UserDbStorageTest {

    private final UserDbStorage userDbStorage;

    @Test
    public void shouldGetAllUsersTest() {
        User firstUser = User.builder()
                .email("firstUser@gmail.com")
                .login("first")
                .name("First Firsted")
                .birthday(LocalDate.of(1970, 2, 2))
                .build();
        User seconUser = User.builder()
                .email("secondUser@yandex.ru")
                .login("second")
                .name("Second Seconded")
                .birthday(LocalDate.of(2009, 8, 1))
                .build();
        User thirdUser = User.builder()
                .email("thirdUser@mail.ru")
                .login("third")
                .name("Third Thirded")
                .birthday(LocalDate.of(2015, 1, 7))
                .build();

        //проверка
        Assertions.assertTrue(userDbStorage.getAll().isEmpty());
        userDbStorage.addUser(firstUser);
        userDbStorage.addUser(seconUser);
        userDbStorage.addUser(thirdUser);
        Assertions.assertTrue(userDbStorage.getAll().size() == 3);

    }

    @Test
    public void shouldGetUserByIdTest() {
        User user = User.builder()
                .email("user@yahoo.com")
                .login("user")
                .name("User Userovich Userov")
                .birthday(LocalDate.of(1974, 4, 6))
                .build();

        User newUser = userDbStorage.addUser(user).get();
        User gottedUser = userDbStorage.getUserById(newUser.getId()).get();

        //проверка
        Assertions.assertEquals(newUser.getEmail(), gottedUser.getEmail());
        Assertions.assertEquals(newUser.getLogin(), gottedUser.getLogin());
        Assertions.assertEquals(newUser.getName(), gottedUser.getName());
        Assertions.assertEquals(newUser.getBirthday(), gottedUser.getBirthday());

    }

    @Test
    public void shouldAddUserTest() {
        User someUser = User.builder()
                .email("someUser@rambler.com")
                .login("some_user")
                .name("Some User")
                .birthday(LocalDate.of(1996, 4, 24))
                .build();

        //проверка
        int usersAmountBefore = userDbStorage.getAll().size();
        User addedSomeUser = userDbStorage.addUser(someUser).get();
        Assertions.assertEquals(usersAmountBefore + 1, userDbStorage.getAll().size());
        User gottedSomeUser = userDbStorage.getUserById(addedSomeUser.getId()).get();
        Assertions.assertEquals(addedSomeUser.getEmail(), gottedSomeUser.getEmail());
        Assertions.assertEquals(addedSomeUser.getLogin(), gottedSomeUser.getLogin());
        Assertions.assertEquals(addedSomeUser.getName(), gottedSomeUser.getName());
        Assertions.assertEquals(addedSomeUser.getBirthday(), gottedSomeUser.getBirthday());

    }

    @Test
    public void shouldUpdateUserTest() {
        User user = User.builder()
                .email("updatingUser@gmail.com")
                .login("updating_user")
                .name("Updating User")
                .birthday(LocalDate.of(1994, 12, 17))
                .build();
        User beforeUpdateUser = userDbStorage.addUser(user).get();
        User updatedUser = User.builder()
                .id(beforeUpdateUser.getId())
                .email("updatedUser@gmail.com")
                .login("updated_user")
                .name("Updated User")
                .birthday(LocalDate.of(1995, 9, 26))
                .build();

        //проверка
        userDbStorage.updateUser(updatedUser);
        User afterUpdateUser = userDbStorage.getUserById(beforeUpdateUser.getId()).get();
        Assertions.assertEquals("updatedUser@gmail.com", afterUpdateUser.getEmail());
        Assertions.assertEquals("updated_user", afterUpdateUser.getLogin());
        Assertions.assertEquals("Updated User", afterUpdateUser.getName());
        Assertions.assertEquals(LocalDate.of(1995, 9, 26), afterUpdateUser.getBirthday());

    }

    @Test
    public void isUserExistTest() {
        User existUser = User.builder()
                .email("existedUser@gmail.com")
                .login("existed_user")
                .name("Existed User")
                .birthday(LocalDate.of(1956, 7, 29))
                .build();
        User notExistUser = User.builder()
                .email("notExistedUser@gmail.com")
                .login("not_existed_user")
                .name("Not Existed User")
                .birthday(LocalDate.of(1900, 5, 15))
                .build();

        User existed = userDbStorage.addUser(existUser).get();

        //проверка
        Assertions.assertTrue(userDbStorage.isUserExist(existed.getId()));
        Assertions.assertFalse(userDbStorage.isUserExist(notExistUser.getId()));

    }

    @Test
    public void shouldMakeManipulationsWithFriends() {
        User userOne = User.builder()
                .email("acceptedUser@gmail.com")
                .login("accepted_user")
                .name("Accepted User")
                .birthday(LocalDate.of(1995, 10, 21))
                .build();
        User userTwo = User.builder()
                .email("subscribedUser@gmail.com")
                .login("subscribed_user")
                .name("Subscribed User")
                .birthday(LocalDate.of(1996, 5, 10))
                .build();

        User acceptedUser = userDbStorage.addUser(userOne).get();
        User subscribedUser = userDbStorage.addUser(userTwo).get();

        //проверки
        Assertions.assertNull(acceptedUser.getFriends());
        userDbStorage.addFriend(acceptedUser.getId(), subscribedUser.getId());
        Set<Long> friendIdsFirst = userDbStorage.getUserFriendsIds(acceptedUser.getId());
        Assertions.assertEquals(friendIdsFirst.size(), 1);
        Assertions.assertTrue(friendIdsFirst.contains(subscribedUser.getId()));
        userDbStorage.removeFriend(acceptedUser.getId(), subscribedUser.getId());
        Set<Long> friendIdsSecond = userDbStorage.getUserFriendsIds(acceptedUser.getId());
        Assertions.assertEquals(friendIdsSecond.size(), 0);

    }

}
