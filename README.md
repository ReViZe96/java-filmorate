# java-filmorate
Template repository for Filmorate project.

## Default database-model
![ER-diagram default](/ER-diagram%20default.png)
## Samples of queries to DB:
### Users:
```
SELECT * FROM Users ---> get all users
SELECT * FROM Users where id = <user_id_value> ---> get user with specified <user_id_value> 
INSERT INTO Users(email, login, name, birthday) VALUES (<email_value>, <login_value>, <name_value>, <birthday_value>) ---> add new user to system
UPDATE Users SET email = <email_value>, login = <login_value>, name = <name_value>, birthday = <birthday_value> where id = <user_id_value> ---> update specified user's fields
```
```
SELECT id FROM Users where id IN (SELECT subscribing_friend_id FROM Friend_relationship WHERE accepting_friend_id = <user_id_value>) ---> get all friends id for user with id = <user_id_value>
INSERT INTO Friend_relationship (accepting_friend_id, subscribing_friend_id) VALUES (<accepting_user_id>, <subscribing_user_id>) ---> make some friend relationship between two users
DELETE FROM Friend_relationship where accepting_friend_id = <accepting_user_id> and subscribing_friend_id = <subscribing_user_id> ---> remove friend relationship between two users
```
### Films:
```
SELECT * FROM Films ---> get all films
SELECT * FROM Films WHERE id = <film_id_value> ---> get film with specified <film_id_value> 
INSERT INTO Films(name, description, release_date, duration, mpa_id) VALUES (<film_name>, <film_description>, <film_release_date>, <film_duration>, <film_mpa_id>) ---> add new film to system
UPDATE Films SET name = <film_name>, description = <film_description>, release_date = <film_release_date>, duration = <film_duration>, mpa_id = <film_mpa_id> WHERE id = <film_id_value> ---> update specified film's fields
```
```
SELECT genre_id FROM Films_genres where film_id = <film_id_value> ---> get all genres ids of specified film  
INSERT INTO Films_genres(film_id, genre_id) VALUES (<film_id_value>, <genre_id_value>) ---> add genre to film
```
```
SELECT * FROM User_likes WHERE film_id = <film_id_value> ---> get all users ids, that liked this film 
INSERT INTO User_likes(film_id, user_id) VALUES (<film_id_value>, <user_id_value>) ---> add like from user for film 
DELETE FROM User_likes where film_id = <film_id_value> and user_id = <user_id_value> ---> unlike film by user
```
### Genres:
```
SELECT * FROM Genres ---> get all film genres
SELECT * FROM Genres WHERE id = <genre_id_value> ---> get genre with specified <genre_id_value> 
INSERT INTO Genres(name) VALUES (<genre_name>) ---> add new genre to system
```
### Mpas:
```
SELECT * FROM Motion_Picture_Associations ---> get all mpas 
SELECT * FROM Motion_Picture_Associations WHERE id = <mpa_id_value> ---> get mpa with specified <mpa_id_value> 
```

## Database model with additional fields for Mpa and FilmGenre entities
![ER-diagram additional](/ER-diagram%20additional.png)


