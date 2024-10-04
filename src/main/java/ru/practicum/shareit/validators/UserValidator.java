package ru.practicum.shareit.validators;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.exception.EmailDoubleException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

@Slf4j
public class UserValidator {

    public static void validateId(Long id) {
        log.info("Проверка наличия id пользователя: {} ", id);
        if (id == null) {
            throw new ValidationException("Id должен быть указан");
        }
    }

    public static void checkEmailIsUnique(Optional<User> user) {
        if (user.isPresent()) {
            throw new EmailDoubleException("Email должен быть уникальным");
        }
    }
}
