package ru.practicum.shareit.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public record ErrorResponse(String error, HttpStatus status, String description) {}
