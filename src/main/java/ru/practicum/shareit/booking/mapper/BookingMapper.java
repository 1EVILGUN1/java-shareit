package ru.practicum.shareit.booking.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingCreatedDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.Collection;
import java.util.stream.Collectors;

@Component
public final class BookingMapper {

    public static BookingDto mapToBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .booker(UserMapper.mapToUserDto(booking.getBooker()))
                .item(ItemMapper.mapToItemDto(booking.getItem()))
                .status(booking.getStatus())
                .build();
    }

    public static Booking mapBookingCreatedDtoToBooking(BookingCreatedDto bookingCreatedDto) {
        return Booking.builder()
                .status(Status.WAITING)
                .start(bookingCreatedDto.getStart())
                .end(bookingCreatedDto.getEnd())
                .build();
    }

    public static BookingItemDto mapBookingToBookingItemDto(Booking booking) {
        return BookingItemDto.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .startDate(booking.getStart())
                .endDate(booking.getEnd())
                .build();
    }

    public static Collection<BookingDto> mapToBookingDtoList(Collection<Booking> bookings) {
        return bookings.stream().map(BookingMapper::mapToBookingDto).collect(Collectors.toList());
    }
}
