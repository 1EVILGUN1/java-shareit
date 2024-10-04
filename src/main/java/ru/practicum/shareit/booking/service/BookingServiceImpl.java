package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.validators.BookingValidator;
import ru.practicum.shareit.booking.dto.BookingAddDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.ItemDoNotBelongToUser;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.validators.UserValidator;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository repository;
    private final UserService userService;
    private final ItemService itemService;

    @Override
    public Booking addBooking(BookingAddDto dto, Long userId) {
        BookingValidator.timeCheck(dto, LocalDateTime.now());
        log.info("Запуск записи бронирования");
        UserValidator.validateId(userId);
        log.info("Проверка наличия пользователя в БД id= " + userId);
        userService.findUserById(userId);
        Item item = itemService.findItemById(dto.getItemId());
        if (!item.isAvailable()) {
            throw new NotAvailableException("Вещь с id= " + item.getId() + " недоступна для бронирования");
        }
        dto.setBooker(userService.findUserById(userId));
        Booking booking = BookingMapper.mapToBookingFromAddDto(dto, itemService);
        booking.setStatus(Status.WAITING);
        return repository.save(booking);
    }

    @Override
    public BookingDto changeStatus(Long id, Boolean approved, Long userId) {
        log.info("Смена статуса бронирования id= " + id);
        BookingValidator.validateId(id);
        UserValidator.validateId(userId);
        log.info("Проверка наличия статуса approved в запросе");
        if (approved == null) {
            throw new ValidationException("Поле approved не должно быть пустым, либо должно быть true или false");
        }
        log.info("Проверка наличия бронирования с id= " + id);
        Booking booking = repository
                .findById(id).orElseThrow(() -> new NotFoundException("Бронирование с id " + id + " не существует"));
        log.info("Проверка владельца бронируемой вещи");
        if (!booking.getItem().getOwner().getId()
                .equals(userId)) {
            throw new ItemDoNotBelongToUser("Пользователь с id " + userId + " не является владельцем бронируемой вещи");
        }
        booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        return BookingMapper.mapToBookingDto(repository.save(booking));
    }

    @Override
    public BookingDto findSpecificBooking(Long id, Long userId) {
        BookingValidator.validateId(id);
        UserValidator.validateId(userId);
        Booking booking = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Такая бронь не найдена"));
        if (booking.getBooker().getId().equals(userId) || booking.getItem().getOwner().getId().equals(userId)) {
            return BookingMapper.mapToBookingDto(booking);
        } else {
            throw new ItemDoNotBelongToUser("Такой брони не существует для данного пользователя");
        }
    }

    @Override
    public List<BookingDto> findAllBookingsOfBooker(Long userId, State state) {
        UserValidator.validateId(userId);
        log.info("Проверка наличия пользователя в БД при получении списка всех бронирований");
        userService.findUserById(userId);
        log.info("STATE " + state);
        List<Booking> bookingList;
        switch (state) {
            case ALL -> bookingList = repository.findAllBookingsByBooker_idOrderByStartDesc(userId);
            case CURRENT -> bookingList = repository
                    .findAllByBooker_idAndStartBeforeAndEndAfterOrderByStartDesc(userId,
                            LocalDateTime.now(), LocalDateTime.now());
            case PAST -> bookingList = repository
                    .findAllByBooker_idAndEndAfterOrderByStartDesc(userId, LocalDateTime.now());
            case FUTURE -> bookingList = repository
                    .findAllByBooker_idAndStartAfterOrderByStartDesc(userId, LocalDateTime.now());
            case WAITING ->
                    bookingList = repository.findAllByBooker_idAndStatusOrderByStartDesc(userId, Status.WAITING);
            case REJECTED ->
                    bookingList = repository.findAllByBooker_idAndStatusOrderByStartDesc(userId, Status.REJECTED);
            default -> throw new IllegalArgumentException("Неизвестное состояние: " + state);
        }
        if (bookingList.isEmpty()) {
            throw new ItemDoNotBelongToUser("Брони не существует для данного пользователя");
        }
        return bookingList.stream()
                .map(BookingMapper::mapToBookingDto)
                .toList();
    }

    @Override
    public List<BookingDto> findAllBookingsOfOwner(Long userId, State state) {
        UserValidator.validateId(userId);
        List<Booking> bookingList;
        switch (state) {
            case ALL -> bookingList = repository.findAllByItem_Owner_idOrderByStartDesc(userId);
            case CURRENT -> bookingList = repository
                    .findAllByItem_Owner_idAndStartBeforeAndEndAfterOrderByStartDesc(userId,
                            LocalDateTime.now(), LocalDateTime.now());
            case PAST -> bookingList = repository
                    .findAllByItem_Owner_idAndEndAfterOrderByStartDesc(userId, LocalDateTime.now());
            case FUTURE -> bookingList = repository
                    .findAllByItem_Owner_idAndStartAfterOrderByStartDesc(userId, LocalDateTime.now());
            case WAITING -> bookingList = repository
                    .findAllByItem_Owner_idAndStatusOrderByStartDesc(userId, Status.WAITING);
            case REJECTED -> bookingList = repository
                    .findAllByItem_Owner_idAndStatusOrderByStartDesc(userId, Status.REJECTED);
            default -> throw new IllegalArgumentException("Неизвестное состояние: " + state);
        }
        if (bookingList.isEmpty()) {
            throw new ItemDoNotBelongToUser("Брони не существует для данного пользователя");
        }
        return bookingList.stream()
                .map(BookingMapper::mapToBookingDto)
                .toList();
    }

    @Override
    public Booking findLastBooking(Item item) {
        return repository
                .findFirstByItem_Owner_idAndStartBeforeOrderByStartDesc(itemService
                        .findItemById(item.getId())
                        .getOwner()
                        .getId(), LocalDateTime.now())
                .orElse(null);
    }

    @Override
    public Booking findFutureBooking(Item item) {
        return repository
                .findFirstByItem_Owner_idAndStartAfterOrderByStartAsc(itemService
                        .findItemById(item.getId()).getOwner()
                        .getId(), LocalDateTime.now()).orElse(null);
    }
}
