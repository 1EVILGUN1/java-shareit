package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@NoArgsConstructor
@Data
public class BookingDto {

    private Long id;

    @NotNull(message = "Time start not must null")
    private LocalDateTime start;

    @NotNull(message = "Time end not must null")
    private LocalDateTime end;

    private Item item;

    private User booker;

    private Status status;

    private State state;
}
