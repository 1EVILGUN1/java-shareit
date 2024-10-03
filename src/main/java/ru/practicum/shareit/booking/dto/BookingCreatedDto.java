package ru.practicum.shareit.booking.dto;

import lombok.Getter;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
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
