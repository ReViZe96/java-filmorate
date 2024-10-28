-- ДЛЯ РАСШИРЕННОЙ МОДЕЛИ:
--insert into Motion_Picture_Associations(id, name, description)
--select 1, 'G', 'Нет возрастных ограничений'
--where not exists (select id from Motion_Picture_Associations where id = 1);
--insert into Motion_Picture_Associations(id, name, description)
--select 2, 'PG', 'Детям рекомендуется смотреть фильм с родителями'
--where not exists (select id from Motion_Picture_Associations where id = 2);
--insert into Motion_Picture_Associations(id, name, description)
--select 3, 'PG-13', 'Детям до 13 лет просмотр не желателен'
--where not exists (select id from Motion_Picture_Associations where id = 3);
--insert into Motion_Picture_Associations(id, name, description)
--select 4, 'R', 'Лицам до 17 лет просматривать фильм можно только в присутствии взрослого'
--where not exists (select id from Motion_Picture_Associations where id = 4);
--insert into Motion_Picture_Associations(id, name, description)
--select 5, 'NC-17', 'Лицам до 18 лет просмотр запрещён'
--where not exists (select id from Motion_Picture_Associations where id = 5);

--insert into Genres(id, english_name, russian_name)
--select 1, 'COMEDY', 'Комедия' where not exists (select id from Genres where id = 1);
--insert into Genres(id, english_name, russian_name)
--select 2, 'DRAMA', 'Драмма' where not exists (select id from Genres where id = 2);
--insert into Genres(id, english_name, russian_name)
--select 3, 'CARTOON', 'Мультфильм' where not exists (select id from Genres where id = 3);
--insert into Genres(id, english_name, russian_name)
--select 4, 'THRILLER', 'Триллер' where not exists (select id from Genres where id = 4);
--insert into Genres(id, english_name, russian_name)
--select 5, 'DOCUMENTARY', 'Документальное кино' where not exists (select id from Genres where id = 5);
--insert into Genres(id, english_name, russian_name)
--select 6, 'ACTION', 'Боевик' where not exists (select id from Genres where id = 6);

insert into Motion_Picture_Associations(id, name)
select 1, 'G' where not exists (select id from Motion_Picture_Associations where id = 1);
insert into Motion_Picture_Associations(id, name)
select 2, 'PG' where not exists (select id from Motion_Picture_Associations where id = 2);
insert into Motion_Picture_Associations(id, name)
select 3, 'PG-13' where not exists (select id from Motion_Picture_Associations where id = 3);
insert into Motion_Picture_Associations(id, name)
select 4, 'R' where not exists (select id from Motion_Picture_Associations where id = 4);
insert into Motion_Picture_Associations(id, name)
select 5, 'NC-17' where not exists (select id from Motion_Picture_Associations where id = 5);

insert into Genres(id, name) select 1, 'Комедия' where not exists (select id from Genres where id = 1);
insert into Genres(id, name) select 2, 'Драма' where not exists (select id from Genres where id = 2);
insert into Genres(id, name) select 3, 'Мультфильм' where not exists (select id from Genres where id = 3);
insert into Genres(id, name) select 4, 'Триллер' where not exists (select id from Genres where id = 4);
insert into Genres(id, name) select 5, 'Документальный' where not exists (select id from Genres where id = 5);
insert into Genres(id, name) select 6, 'Боевик' where not exists (select id from Genres where id = 6);
