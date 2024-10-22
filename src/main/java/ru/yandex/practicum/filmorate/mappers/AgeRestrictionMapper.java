package ru.yandex.practicum.filmorate.mappers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.AgeRestrictionDto;
import ru.yandex.practicum.filmorate.model.AgeRestriction;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AgeRestrictionMapper {

    public static AgeRestrictionDto mapToAgeRestrictionDto(AgeRestriction ageRestriction) {
       return new AgeRestrictionDto(
                ageRestriction.getId(),
                ageRestriction.getName(),
                ageRestriction.getDescription()
        );
    }

}
