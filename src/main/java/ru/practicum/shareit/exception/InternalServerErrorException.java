package ru.practicum.shareit.exception;

public class InternalServerErrorException extends RuntimeException {
    public InternalServerErrorException(String m) {
        super(m);
    }
}
