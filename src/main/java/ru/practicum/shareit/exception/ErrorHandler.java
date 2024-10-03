package ru.practicum.shareit.exception;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleThrowable(final Throwable throwable) {
        log.error("Ошибка! {}", throwable.getMessage());
        return new ResponseEntity<>(
                Map.of("error", throwable.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleInvalidId(final IllegalArgumentException e) {
        log.error("Ошибка! {}", e.getMessage());
        return new ResponseEntity<>(
                Map.of("error", e.getMessage()), HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleValidationException(final ValidationException e) {
        log.error("Ошибка! {}", e.getMessage());
        return new ResponseEntity<>(
                Map.of("error", e.getMessage()), HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleBadRequestException(final BadRequestException e) {
        log.error("Ошибка! {}", e.getMessage());
        return new ResponseEntity<>(
                Map.of("error", e.getMessage()), HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
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
    public ResponseEntity<Map<String, String>> handleNotFoundException(final NotFoundException e) {
        log.error("Ошибка! {}", e.getMessage());
        return new ResponseEntity<>(
                Map.of("error", e.getMessage()), HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleInternalServerErrorException(final InternalServerErrorException e) {
        log.error("Ошибка! {}", e.getMessage());
        return new ResponseEntity<>(
                Map.of("error", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
