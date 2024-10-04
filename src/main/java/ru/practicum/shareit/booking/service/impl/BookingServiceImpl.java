package ru.practicum.shareit.booking.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreatedDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.InternalServerErrorException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collection;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final ItemService itemService;
    private final UserService userService;
    private final BookingRepository repository;


    @Override
    @Transactional
    public BookingDto addBooking(BookingCreatedDto bookingCreatedDto, long userId) {
        Booking booking = BookingMapper.mapBookingCreatedDtoToBooking(bookingCreatedDto);
        Item item = itemService.checkItemDto(bookingCreatedDto.getItemId());
        if (!item.getAvailable()) {
            throw new InternalServerErrorException(String.format("Вещь с ID %d нельзя арендовать", item.getId()));
        }
        if (booking.getEnd().isBefore(booking.getStart()) || booking.getStart().equals(booking.getEnd())) {
            throw new BadRequestException("Неверная дата бронировния");
        }
        booking.setItem(item);
        User user = userService.checkUserDto(userId);
        if (user.equals(item.getUser())) {
            throw new NotFoundException("Владелец вещи не может ее забронировать!");
        }
        booking.setBooker(user);
        return BookingMapper.mapToBookingDto(repository.save(booking));
    }

    @Override
    @Transactional
    public BookingDto approveBooking(long bookingId, boolean status, long userId) {
        Booking booking = checkBookingDto(bookingId);
        if (!booking.getItem().getUser().getId().equals(userId)) {
            throw new InternalServerErrorException("Подтверждение брони может быть выполнено только владельцем!");
        }
        if (status) {
            if (booking.getStatus() == Status.APPROVED) {
                throw new BadRequestException("Нельзя подтвердить бронь после подтверждения!");
            }
            booking.setStatus(Status.APPROVED);
        } else {
            if (booking.getStatus() == Status.REJECTED) {
                throw new BadRequestException("Нельзя отклонить бронь после отклонения!");
            }
            booking.setStatus(Status.REJECTED);
        }
        repository.save(booking);
        return BookingMapper.mapToBookingDto(booking);
    }

    @Override
    public BookingDto getBookingById(long bookingId, long userId) {
        Booking booking = checkBookingDto(bookingId);
        return checkBookingOwnerOrItemOwner(booking, userId);
    }

    @Override
    public Collection<BookingDto> getUserBookings(String state, long userId) {
        userService.checkUserDto(userId);
        switch (state) {
            case "ALL":
                return BookingMapper.mapToBookingDtoList(
                        repository.findAllByBookerIdOrderByStartDesc(userId));
            case "PAST":
                return BookingMapper.mapToBookingDtoList(
                        repository.findAllByEndBeforeAndBookerIdOrderByStartDesc(LocalDateTime.now(), userId));
            case "FUTURE":
                return BookingMapper.mapToBookingDtoList(
                        repository.findAllByStartAfterAndBookerIdOrderByStartDesc(LocalDateTime.now(), userId));
            case "CURRENT":
                return BookingMapper.mapToBookingDtoList(
                        repository.findAllByEndAfterAndStartBeforeAndBookerIdOrderByStartDesc(
                                LocalDateTime.now(), LocalDateTime.now(), userId));
            case "WAITING":
                return BookingMapper.mapToBookingDtoList(
                        repository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.WAITING));
            case "REJECTED":
                return BookingMapper.mapToBookingDtoList(
                        repository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.REJECTED));
            default:
                throw new InternalServerErrorException("Unknown state: " + state);
        }
    }

    @Override
    public Collection<BookingDto> getAllBookingsByUserOwner(String state, long userId) {
        userService.checkUserDto(userId);
        switch (state) {
            case "ALL":
                return BookingMapper.mapToBookingDtoList(
                        repository.findAllByItemUserIdOrderByStartDesc(userId));
            case "PAST":
                return BookingMapper.mapToBookingDtoList(
                        repository.findByEndBeforeAndItemUserIdOrderByStartDesc(LocalDateTime.now(), userId));
            case "FUTURE":
                return BookingMapper.mapToBookingDtoList(
                        repository.findAllByItemUserIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now()));
            case "CURRENT":
                return BookingMapper.mapToBookingDtoList(
                        repository.findAllByEndAfterAndStartBeforeAndItemUserIdOrderByStartDesc(
                                LocalDateTime.now(), LocalDateTime.now(), userId));
            case "WAITING":
                return BookingMapper.mapToBookingDtoList(
                        repository.findAllByItemUserIdAndStatusOrderByStartDesc(userId, Status.WAITING));
            case "REJECTED":
                return BookingMapper.mapToBookingDtoList(
                        repository.findAllByItemUserIdAndStatusOrderByStartDesc(userId, Status.REJECTED));
            default:
                throw new InternalServerErrorException("Unknown state: " + state);
        }
    }

    @Override
    public Booking checkBookingDto(long bookingId) {
        return repository.findById(bookingId).orElseThrow(() ->
                new NotFoundException(String.format("Бронь с ID %d не найдена", bookingId)));
    }

    private BookingDto checkBookingOwnerOrItemOwner(Booking booking, long userId) {
        if (booking.getItem().getUser().getId().equals(userId) || booking.getBooker().getId().equals(userId)) {
            return BookingMapper.mapToBookingDto(booking);
        }
        throw new NotFoundException("Только владелец брони или вещи может получить данные!");
    }
}
