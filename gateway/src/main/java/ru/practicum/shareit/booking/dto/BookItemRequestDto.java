package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookItemRequestDto {

    @NotNull(message = "Time start booking not must null")
    private LocalDateTime start;

    @NotNull(message = "Time end booking no must null")
    private LocalDateTime end;

    @NotNull(message = "The item to be booked must be specified")
    private long itemId;
}
