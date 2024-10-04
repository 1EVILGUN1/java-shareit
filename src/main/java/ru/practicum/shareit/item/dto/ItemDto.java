package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingInItemDto;

import java.util.List;

@NoArgsConstructor
@Data
public class ItemDto {

    private Long id;

    @NotBlank(message = "Название не может быть пустым")
    private String name;

    @NotBlank(message = "Описание не может быть пустым")
    private String description;

    @NotNull(message = "Статус доступности не может быть пустым")
    private Boolean available;

    private BookingInItemDto lastBooking;

    private BookingInItemDto nextBooking;

    private List<CommentDto> comments;

    public Boolean isAvailable() {
        return available;
    }
}
