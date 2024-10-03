package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Map<String, String>> handleThrowable(final Throwable throwable) {
        log.error("Ошибка! {}", throwable.getMessage());
        return new ResponseEntity<>(
                Map.of("error", throwable.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Map<String, String>> handleInvalidId(final IllegalArgumentException e) {
        log.error("Ошибка! {}", e.getMessage());
        return new ResponseEntity<>(
                Map.of("error", e.getMessage()), HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Map<String, String>> handleValidationException(final ValidationException e) {
        log.error("Ошибка! {}", e.getMessage());
        return new ResponseEntity<>(
                Map.of("error", e.getMessage()), HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleBadRequestException(final BadRequestException e) {
        log.error("Ошибка! {}", e.getMessage());
        return new ResponseEntity<>(
                Map.of("error", e.getMessage()), HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> notFoundException(final NotFoundException e) {
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Map<String, String>> handleInternalServerErrorException(final InternalServerErrorException e) {
        log.error("Ошибка! {}", e.getMessage());
        return new ResponseEntity<>(
                Map.of("error", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
