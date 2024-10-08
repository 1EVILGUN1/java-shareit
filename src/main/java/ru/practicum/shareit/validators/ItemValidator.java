package ru.practicum.shareit.validators;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.exception.ValidationException;

@Slf4j
public class ItemValidator {

    public static void validateId(Long id) {
        log.info("Проверка наличия id вещи: {} ", id);
        if (id == null) {
            throw new ValidationException("Id должен быть указан");
        }
    }
}
