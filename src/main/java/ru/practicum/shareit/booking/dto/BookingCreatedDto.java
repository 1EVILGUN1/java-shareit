package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class BookingCreatedDto {
    private Long itemId;
    @Future
    @NotNull(message = "Время начала аренды не может быть NULL")
    private LocalDateTime start;
    @Future
    @NotNull(message = "Время конца аренды не может быть NULL")
    private LocalDateTime end;
}
