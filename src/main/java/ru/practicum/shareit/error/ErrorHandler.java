package ru.practicum.shareit.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.*;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler({MethodArgumentNotValidException.class, ValidationException.class, NotAvailableException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(final Exception e) {
        log.error("Ошибка валидации данных: {}.", e.getMessage());
        return new ErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST, "Ошибка валидации данных.");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(final NotFoundException e) {
        log.error("Ошибка с входными параметрами: {}.", e.getMessage());
        return new ErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND, "Ошибка с входными параметрами.");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(final Throwable e) {
        log.error("Возникла ошибка: {}.", e.getMessage());
        return new ErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, "Возникла ошибка.");
    }

    @ExceptionHandler(EmailDoubleException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleEmailDoubleException(final EmailDoubleException e) {
        log.error("Ошибка с входными параметрами(повтор email): {}.", e.getMessage());
        return new ErrorResponse(e.getMessage(), HttpStatus.CONFLICT, "Ошибка с входными параметрами(повтор email).");
    }

    @ExceptionHandler(ItemDoNotBelongToUser.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleItemDoNotBelongToUser(final ItemDoNotBelongToUser e) {
        log.error("Ошибка - для текущего пользователя в доступе отказано: {}.", e.getMessage());
        return new ErrorResponse(e.getMessage(), HttpStatus.FORBIDDEN, "Ошибка - для текущего пользователя в доступе отказано.");
    }
}
