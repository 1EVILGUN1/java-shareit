package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllBookingsByBooker_idOrderByStartDesc(Long bookerId);

    List<Booking> findAllByBooker_idAndStartBeforeAndEndAfterOrderByStartDesc(Long bookerId,
                                                                              LocalDateTime nowStart,
                                                                              LocalDateTime nowEnd);

    List<Booking> findAllByBooker_idAndEndAfterOrderByStartDesc(Long bookerId,
                                                                LocalDateTime nowEnd);

    List<Booking> findAllByBooker_idAndStartAfterOrderByStartDesc(Long bookerId,
                                                                  LocalDateTime nowStart);

    List<Booking> findAllByBooker_idAndStatusOrderByStartDesc(Long bookerId, Status status);

    List<Booking> findAllByItem_Owner_idOrderByStartDesc(Long ownerId);

    List<Booking> findAllByItem_Owner_idAndStartBeforeAndEndAfterOrderByStartDesc(
            Long ownerId, LocalDateTime nowStart, LocalDateTime nowEnd);

    List<Booking> findAllByItem_Owner_idAndEndAfterOrderByStartDesc(Long bookerId,
                                                                    LocalDateTime nowEnd);

    List<Booking> findAllByItem_Owner_idAndStartAfterOrderByStartDesc(Long bookerId,
                                                                      LocalDateTime nowStart);

    List<Booking> findAllByItem_Owner_idAndStatusOrderByStartDesc(Long bookerId, Status status);

    Optional<Booking> findFirstByItem_Owner_idAndStartBeforeOrderByStartDesc(Long ownerId,
                                                                             LocalDateTime nowTime);

    Optional<Booking> findFirstByItem_Owner_idAndStartAfterOrderByStartAsc(Long ownerId,
                                                                           LocalDateTime nowTome);

    Optional<Booking> findByItemIdAndBookerIdAndEndBefore(Long itemId, Long bookerId, LocalDateTime now);
}
