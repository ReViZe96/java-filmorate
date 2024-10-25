-- ДЛЯ РАСШИРЕННОЙ МОДЕЛИ:
--insert into Motion_Picture_Associations (name, description) values ('G', 'Нет возрастных ограничений');
--insert into Motion_Picture_Associations (name, description) values ('PG', 'Детям рекомендуется смотреть фильм с родителями');
--insert into Motion_Picture_Associations (name, description) values ('PG_13', 'Детям до 13 лет просмотр не желателен');
--insert into Motion_Picture_Associations (name, description) values ('R', 'Лицам до 17 лет просматривать фильм можно только в присутствии взрослого');
--insert into Motion_Picture_Associations (name, description) values ('NC_17', 'Лицам до 18 лет просмотр запрещён');

--insert into Genres (english_name, russian_name) values ('COMEDY', 'Комедия');
--insert into Genres (english_name, russian_name) values ('DRAMA', 'Драмма');
--insert into Genres (english_name, russian_name) values ('CARTOON', 'Мультфильм');
--insert into Genres (english_name, russian_name) values ('THRILLER', 'Триллер');
--insert into Genres (english_name, russian_name) values ('ACTION', 'Боевик');
--insert into Genres (english_name, russian_name) values ('SCIFI', 'Научная фантастика');
--insert into Genres (english_name, russian_name) values ('DOCUMENTARY', 'Документальное кино');

insert into Motion_Picture_Associations (name) values ('G');
insert into Motion_Picture_Associations (name) values ('PG');
insert into Motion_Picture_Associations (name) values ('PG-13');
insert into Motion_Picture_Associations (name) values ('R');
insert into Motion_Picture_Associations (name) values ('NC-17');

insert into Genres (name) values ('Комедия');
insert into Genres (name) values ('Драма');
insert into Genres (name) values ('Мультфильм');
insert into Genres (name) values ('Триллер');
insert into Genres (name) values ('Документальный');
insert into Genres (name) values ('Боевик');