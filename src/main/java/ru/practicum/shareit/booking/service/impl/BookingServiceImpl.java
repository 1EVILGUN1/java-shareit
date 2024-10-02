package ru.practicum.shareit.booking.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.InternalServerErrorException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collection;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;

    @Override
    @Transactional
    public BookingDto saveBooking(BookingDto booking, long userId) {
        Booking booker = BookingMapper.mapToBooking(booking);
        Item item = ItemMapper.mapToItem(itemService.findById(booking.getId(), userId));
        if (!item.getAvailable()) {
            throw new BadRequestException(String.format("Вещь с ID %d нельзя арендовать", item.getId()));
        }
        if (booker.getEnd().isBefore(booker.getStart()) || booker.getStart().equals(booker.getEnd())) {
            throw new BadRequestException("Неверная дата бронирования");
        }
        booker.setItem(item);
        User user = UserMapper.mapToUser(userService.findById(userId));
        if (user.equals(item.getUser())) {
            throw new NotFoundException("Владелец вещи не может ее забронировать!");
        }
        booker.setBooker(user);
        Booking savedBooker = bookingRepository.save(booker);
        return BookingMapper.mapToBookingDto(savedBooker);
    }

    @Override
    @Transactional
    public BookingDto approveBooking(long bookingId, boolean status, long userId) {
        Booking booking = BookingMapper.mapToBooking(findBookingById(bookingId, userId));
        if (!booking.getItem().getUser().getId().equals(userId)) {
            throw new NotFoundException("Подтверждение брони может быть выполнено только владельцем!");
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
        Booking approvedBooking = bookingRepository.save(booking);
        return BookingMapper.mapToBookingDto(approvedBooking);
    }

    @Override
    public BookingDto findBookingById(long bookingId, long userId) {
        Booking booking = checkBookingDtoById(bookingId);
        return checkBookingOwnerOrItemOwner(booking, userId);
    }

    @Override
    public Collection<BookingDto> userBookings(String state, long userId) {
        userService.findById(userId);
        return switch (state) {
            case "ALL" -> BookingMapper.mapToBookingDtos(
                    bookingRepository.findAllByBookerIdOrderByStartDesc(userId));
            case "PAST" -> BookingMapper.mapToBookingDtos(
                    bookingRepository.findAllByEndBeforeAndBookerIdOrderByStartDesc(LocalDateTime.now(), userId));
            case "FUTURE" -> BookingMapper.mapToBookingDtos(
                    bookingRepository.findAllByStartAfterAndBookerIdOrderByStartDesc(LocalDateTime.now(), userId));
            case "CURRENT" -> BookingMapper.mapToBookingDtos(
                    bookingRepository.findAllByEndAfterAndStartBeforeAndBookerIdOrderByStartDesc(
                            LocalDateTime.now(), LocalDateTime.now(), userId));
            case "WAITING" -> BookingMapper.mapToBookingDtos(
                    bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.WAITING));
            case "REJECTED" -> BookingMapper.mapToBookingDtos(
                    bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.REJECTED));
            default -> throw new InternalServerErrorException("Unknown state: " + state);
        };
    }

    @Override
    public Collection<BookingDto> allBookingsByUserOwner(String state, long userId) {
        userService.findById(userId);
        return switch (state) {
            case "ALL" -> BookingMapper.mapToBookingDtos(
                    bookingRepository.findAllByItemUserIdOrderByStartDesc(userId));
            case "PAST" -> BookingMapper.mapToBookingDtos(
                    bookingRepository.findByEndBeforeAndItemUserIdOrderByStartDesc(LocalDateTime.now(), userId));
            case "FUTURE" -> BookingMapper.mapToBookingDtos(
                    bookingRepository.findAllByItemUserIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now()));
            case "CURRENT" -> BookingMapper.mapToBookingDtos(
                    bookingRepository.findAllByEndAfterAndStartBeforeAndItemUserIdOrderByStartDesc(
                            LocalDateTime.now(), LocalDateTime.now(), userId));
            case "WAITING" -> BookingMapper.mapToBookingDtos(
                    bookingRepository.findAllByItemUserIdAndStatusOrderByStartDesc(userId, Status.WAITING));
            case "REJECTED" -> BookingMapper.mapToBookingDtos(
                    bookingRepository.findAllByItemUserIdAndStatusOrderByStartDesc(userId, Status.REJECTED));
            default -> throw new InternalServerErrorException("Unknown state: " + state);
        };
    }

    private Booking checkBookingDtoById(long id) {
        return bookingRepository.findById(id).orElseThrow(() ->
                new NotFoundException(String.format("Бронь с ID %d не найдена", id)));
    }

    private BookingDto checkBookingOwnerOrItemOwner(Booking booking, long userId) {
        if (booking.getItem().getUser().getId().equals(userId) || booking.getBooker().getId().equals(userId)) {
            return BookingMapper.mapToBookingDto(booking);
        }
        throw new NotFoundException("Только владелец брони или вещи может получить данные!");
    }
}
