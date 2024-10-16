package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingAddDto;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ItemDoNotBelongToUser;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository repository;
    private final UserService userService;
    private final ItemService itemService;

    @Override
    @Transactional
    public Booking addBooking(BookingAddDto dto, Long userId) {
        log.debug("Charge write booking");
        log.debug("Check user with DB id= " + userId);
        userService.findById(userId);
        Item item = itemService.getItem(dto.getItemId());
        if (!item.isAvailable()) {
            throw new NotAvailableException("Item with id= " + item.getId() + " does not belong to the booking");
        }
        dto.setBooker(userService.findById(userId));
        Booking booking = BookingMapper.mapToBookingFromAddDto(dto, itemService);
        booking.setStatus(Status.WAITING);
        return repository.save(booking);
    }

    @Override
    @Transactional
    public Booking changeStatus(Long id, Boolean approved, Long userId) {
        log.debug("Change of booking status with id= " + id);
        log.debug("Check status approved int request");
        if (approved == null) {
            throw new ValidationException("Field approved not must blank, else true or false");
        }
        log.debug("Check booking with id= " + id);
        Booking booking = repository
                .findById(id).orElseThrow(() -> new NotFoundException("Booking with id " + id + " does not exist"));
        log.debug("Check user booking item");
        if (!booking.getItem().getOwner().getId()
                .equals(userId)) {
            throw new ItemDoNotBelongToUser("User with id " + userId + " does not owner booking item");
        }
        booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        return repository.save(booking);
    }

    @Override
    public Booking findSpecificBooking(Long id, Long userId) {
        Booking booking = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Booking does not exist"));
        if (booking.getBooker().getId().equals(userId) || booking.getItem().getOwner().getId().equals(userId)) {
            return booking;
        } else {
            throw new ItemDoNotBelongToUser("Booking does not belong to user");
        }
    }

    @Override
    public List<Booking> findAllBookingsOfBooker(Long userId, State state) {
        log.info("Check user in DB get list all booking");
        userService.findById(userId);
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
            case REJECTED -> bookingList = repository.findAllByBooker_idAndStatusOrderByStartDesc(userId,
                    Status.REJECTED);
            default -> throw new IllegalArgumentException("Unknown state: " + state);
        }
        if (bookingList.isEmpty()) {
            bookingList = Collections.emptyList();
        }
        return bookingList;
    }

    @Override
    public List<Booking> findAllBookingsOfOwner(Long userId, State state) {
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
            default -> throw new IllegalArgumentException("Unknown state: " + state);
        }
        if (bookingList.isEmpty()) {
            throw new ItemDoNotBelongToUser("Booking does not exist to user");
        }
        return bookingList;
    }

    @Override
    public Booking findLastBooking(Item item) {
        return repository
                .findFirstByItem_Owner_idAndStartBeforeOrderByStartDesc(itemService
                        .getItem(item.getId())
                        .getOwner()
                        .getId(), LocalDateTime.now())
                .orElse(null);
    }

    @Override
    public Booking findFutureBooking(Item item) {
        return repository
                .findFirstByItem_Owner_idAndStartAfterOrderByStartAsc(itemService
                        .getItem(item.getId()).getOwner()
                        .getId(), LocalDateTime.now()).orElse(null);
    }
}
