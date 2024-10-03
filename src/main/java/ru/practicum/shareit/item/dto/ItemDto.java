package ru.practicum.shareit.item.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

@Data
@NotNull
public class ItemDto {
    private Long id;
    @NotBlank(message = "Имя не может быть пустым!")
    private String name;
    @NotNull(message = "Описание не может быть пустым!")
    private String description;
    @NotNull(message = "Статус для аренды не может быть пустым!")
    private Boolean available;
    private BookingDto lastBooking;
    private BookingDto nextBooking;
    private List<CommentDto> comments;
}
