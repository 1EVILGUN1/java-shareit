package ru.practicum.shareit.error;

import org.springframework.http.HttpStatus;

public record ErrorResponse(String error, HttpStatus status, String description) {
}

