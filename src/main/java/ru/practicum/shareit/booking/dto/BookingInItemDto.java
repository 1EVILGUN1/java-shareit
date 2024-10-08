package ru.practicum.shareit.booking.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.enums.Status;

import java.time.LocalDateTime;

@NoArgsConstructor
@Data
public class BookingInItemDto {

    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Long bookerId;
    private Status status;
    private State state;
}
