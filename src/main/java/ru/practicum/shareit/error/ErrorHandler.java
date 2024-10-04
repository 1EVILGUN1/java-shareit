package ru.practicum.shareit.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.*;

import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler({MethodArgumentNotValidException.class, ValidationException.class, NotAvailableException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationException(final Exception e) {
        log.error("Ошибка валидации данных: {}.", e.getMessage());
        return Map.of(
                "error", "Ошибка валидации данных",
                "description", e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFoundException(final NotFoundException e) {
        log.error("Ошибка с входными параметрами: {}.", e.getMessage());
        return Map.of(
                "error", "Ошибка с входными параметрами.",
                "description", e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleThrowable(final Throwable e) {
        log.error("Возникла ошибка: {}.", e.getMessage());
        return Map.of(
                "error", "Возникла ошибка.",
                "description", e.getMessage()
        );
    }

    @ExceptionHandler(EmailDoubleException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleEmailDoubleException(final EmailDoubleException e) {
        log.error("Ошибка с входными параметрами(повтор email): {}.", e.getMessage());
        return Map.of(
                "error", "Повтор email",
                "description", e.getMessage()
        );
    }

    @ExceptionHandler(ItemDoNotBelongToUser.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Map<String, String> handleItemDoNotBelongToUser(final ItemDoNotBelongToUser e) {
        log.error("Ошибка - для текущего пользователя в доступе отказано: {}.", e.getMessage());
        return Map.of(
                "error", "Ошибка - для текущего пользователя в доступе отказано",
                "description", e.getMessage()
        );
    }
}
