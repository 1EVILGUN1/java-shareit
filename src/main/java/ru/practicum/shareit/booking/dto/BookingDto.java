package ru.practicum.shareit.booking.dto;


import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

@Data
public class BookingDto {
    private Long id;
    @Future
    @NotNull(message = "Время начала аренды не может быть NULL")
    private LocalDateTime start;
    @Future
    @NotNull(message = "Время конца аренды не может быть NULL")
    private LocalDateTime end;
    private Status status;
    private ItemDto item;
    private UserDto booker;
}
