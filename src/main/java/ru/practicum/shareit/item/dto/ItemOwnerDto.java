package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingInItemDto;

import java.util.List;

@NoArgsConstructor
@Data
public class ItemOwnerDto {
    private String name;
    private String description;
    private BookingInItemDto lastBooking;
    private BookingInItemDto nextBooking;
    private List<CommentDto> comments;
}
