package ru.practicum.shareit.exception;

public class ItemDoNotBelongToUser extends RuntimeException {
    public ItemDoNotBelongToUser(String message) {
        super(message);
    }
}
