package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class BookingAddDto {

    private Long id;

    @NotNull(message = "Time start not must null")
    private LocalDateTime start;

    @NotNull(message = "Time end not must null")
    private LocalDateTime end;

    @NotNull(message = "Booking item not must null")
    private Long itemId;

    private User booker;

    private Status status;

    private State state;
}
