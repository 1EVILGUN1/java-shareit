package ru.practicum.shareit.booking.enums;

import java.util.Optional;
import java.util.stream.Stream;

public enum BookingState {
    ALL,
    CURRENT,
    FUTURE,
    PAST,
    REJECTED,
    WAITING;

    public static Optional<BookingState> from(String stringState) {
        return Stream.of(values())
                .filter(state -> state.name().equalsIgnoreCase(stringState))
                .findFirst();
    }
}
