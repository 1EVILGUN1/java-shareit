package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingAddDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInItemDto;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;

public class BookingMapper {

    public static BookingDto mapToBookingDto(Booking booking) {
        BookingDto dto = new BookingDto();
        dto.setId(booking.getId());
        dto.setBooker(booking.getBooker());
        dto.setItem(booking.getItem());
        dto.setStatus(booking.getStatus());
        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());
        dto.setState(setState(booking));
        return dto;
    }

    public static BookingInItemDto mapToItemBookingDto(Booking booking) {
        BookingInItemDto dto = new BookingInItemDto();
        dto.setId(booking.getId());
        dto.setBookerId(booking.getBooker().getId());
        dto.setStatus(booking.getStatus());
        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());
        dto.setState(setState(booking));
        return dto;
    }

    public static Booking mapToBookingFromAddDto(BookingAddDto dto, ItemService itemService) {
        Booking booking = new Booking();
        booking.setBooker(dto.getBooker());
        booking.setItem(itemService.getItem(dto.getItemId()));
        booking.setStatus(dto.getStatus());
        booking.setEnd(dto.getEnd());
        booking.setStart(dto.getStart());
        return booking;
    }

    private static State setState(Booking booking) {
        State state;
        if (booking.getStatus() == Status.REJECTED) {
            state = State.REJECTED;
        } else if (booking.getStatus() == Status.WAITING) {
            state = State.WAITING;
        } else if (booking.getEnd().isBefore(LocalDateTime.now())) {
            state = State.PAST;
        } else if (booking.getStart().isAfter(LocalDateTime.now())) {
            state = State.FUTURE;
        } else {
            state = State.CURRENT;
        }
        return state;
    }
}
