package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingCreatedDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.InternalServerErrorException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final ItemService itemService;
    private final UserService userService;
    private final BookingRepository repository;

    @Override
    @Transactional
    public BookingDto addBooking(BookingCreatedDto bookingCreatedDto, long userId) {
        userService.checkUserDto(userId);
        Booking booking = BookingMapper.mapBookingCreatedDtoToBooking(bookingCreatedDto);
        Item item = itemService.checkItemDto(bookingCreatedDto.getItemId());
        if (!item.getAvailable()) {
            throw new BadRequestException(String.format("Вещь с ID %d нельзя арендовать", item.getId()));
        }
        if (booking.getEnd().isBefore(booking.getStart()) || booking.getStart().equals(booking.getEnd())) {
            throw new BadRequestException("Неверная дата бронирования");
        }
        booking.setItem(item);
        User user = userService.checkUserDto(userId);
        if (user.equals(item.getUser())) {
            throw new NotFoundException("Владелец вещи не может ее забронировать!");
        }
        booking.setBooker(user);
        Booking savedBooking = repository.save(booking);
        return BookingMapper.mapToBookingDto(savedBooking);
    }

    @Override
    @Transactional
    public BookingDto approveBooking(long bookingId, boolean status, long userId) {
        Booking booking = checkBookingDto(bookingId);
        userService.checkUserDto(userId);
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
        Booking approvedBooking = repository.save(booking);
        return BookingMapper.mapToBookingDto(approvedBooking);
    }

    @Override
    public BookingDto findById(long bookingId, long userId) {
        Booking booking = checkBookingDto(bookingId);
        return checkBookingOwnerOrItemOwner(booking, userId);
    }

    @Override
    public Collection<BookingDto> getUserBookings(String state, long userId) {
        userService.checkUserDto(userId);
        Map<String, Function<Long, Collection<Booking>>> stateToRepositoryCalls = Map.of(
                "ALL", id -> repository.findAllByBookerIdOrderByStartDesc(id),
                "PAST", id -> repository.findAllByEndBeforeAndBookerIdOrderByStartDesc(LocalDateTime.now(), id),
                "FUTURE", id -> repository.findAllByStartAfterAndBookerIdOrderByStartDesc(LocalDateTime.now(), id),
                "CURRENT", id -> repository.findAllByEndAfterAndStartBeforeAndBookerIdOrderByStartDesc(
                        LocalDateTime.now(), LocalDateTime.now(), id),
                "WAITING", id -> repository.findAllByBookerIdAndStatusOrderByStartDesc(id, Status.WAITING),
                "REJECTED", id -> repository.findAllByBookerIdAndStatusOrderByStartDesc(id, Status.REJECTED)
        );

        Function<Long, Collection<Booking>> repositoryCall = stateToRepositoryCalls.get(state);
        if (repositoryCall == null) {
            throw new InternalServerErrorException("Unknown state: " + state);
        }

        return BookingMapper.mapToBookingDtoList(repositoryCall.apply(userId));
    }


    @Override
    public Collection<BookingDto> getAllBookingsByUserOwner(String state, long userId) {
        userService.checkUserDto(userId);
        Map<String, Function<Long, Collection<Booking>>> stateToRepositoryCalls = Map.of(
                "ALL", id -> repository.findAllByItemUserIdOrderByStartDesc(id),
                "PAST", id -> repository.findByEndBeforeAndItemUserIdOrderByStartDesc(LocalDateTime.now(), id),
                "FUTURE", id -> repository.findAllByItemUserIdAndStartAfterOrderByStartDesc(id, LocalDateTime.now()),
                "CURRENT", id -> repository.findAllByEndAfterAndStartBeforeAndItemUserIdOrderByStartDesc(
                        LocalDateTime.now(), LocalDateTime.now(), id),
                "WAITING", id -> repository.findAllByItemUserIdAndStatusOrderByStartDesc(id, Status.WAITING),
                "REJECTED", id -> repository.findAllByItemUserIdAndStatusOrderByStartDesc(id, Status.REJECTED)
        );

        Function<Long, Collection<Booking>> repositoryCall = stateToRepositoryCalls.get(state);
        if (repositoryCall == null) {
            throw new InternalServerErrorException("Unknown state: " + state);
        }
        return BookingMapper.mapToBookingDtoList(repositoryCall.apply(userId));
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
