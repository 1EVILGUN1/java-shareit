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
        log.error("Error validate date: {}.", e.getMessage());
        return new ErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST, "Error validate date");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(final NotFoundException e) {
        log.error("Error with input parameters: {}.", e.getMessage());
        return new ErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND, "Error with input parameters");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(final Throwable e) {
        log.error("An error occurred: {}.", e.getMessage());
        return new ErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred.");
    }

    @ExceptionHandler(EmailDoubleException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleEmailDoubleException(final EmailDoubleException e) {
        log.error("Error with input parameters (repeat email): {}.", e.getMessage());
        return new ErrorResponse(e.getMessage(), HttpStatus.CONFLICT, "Error with input parameters (repeat email)");
    }

    @ExceptionHandler(ItemDoNotBelongToUser.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleItemDoNotBelongToUser(final ItemDoNotBelongToUser e) {
        log.error("Error - Access is denied for the current user: {}.", e.getMessage());
        return new ErrorResponse(e.getMessage(), HttpStatus.FORBIDDEN, "Error - Access is denied for the current user");
    }
}
