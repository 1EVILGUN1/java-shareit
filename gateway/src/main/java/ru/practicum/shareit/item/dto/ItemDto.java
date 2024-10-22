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

    @NotBlank(message = "Name not must null")
    private String name;

    @NotBlank(message = "Description not must null")
    private String description;

    @NotNull(message = "Available not must null")
    private Boolean available;

    private BookingInItemDto lastBooking;

    private BookingInItemDto nextBooking;

    private List<CommentDto> comments;

    private Long requestId;

    public Boolean isAvailable() {
        return available;
    }
}
