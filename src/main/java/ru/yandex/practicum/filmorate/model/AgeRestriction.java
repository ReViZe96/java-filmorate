package ru.yandex.practicum.filmorate.model;

import lombok.Getter;

@Getter
public enum AgeRestriction {
    G(1L, "General Audiences", "Нет возрастных ограничений"),
    PG(2L, "Parental Guidance Suggested", "Детям рекомендуется смотреть фильм с родителями"),
    PG_13(3L, "Parents Strongly Cautioned", "Детям до 13 лет просмотр не желателен"),
    R(4L, "Restricted", "Лицам до 17 лет просматривать фильм можно только в присутствии взрослого"),
    NC_17(5L, "Adults Only", "лицам до 18 лет просмотр запрещён");

    private final Long id;
    private final String name;
    private final String description;

    AgeRestriction(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }
}
