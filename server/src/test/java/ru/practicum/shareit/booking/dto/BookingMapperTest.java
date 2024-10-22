package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingMapperTest {

    @Mock
    private ItemService itemService;

    private Booking booking;

    @BeforeEach
    void setUp() {
        User owner = new User(1L, "name", "ameil@email.ru");
        booking = new Booking();
        booking.setId(1L);
        booking.setBooker(new User(1L, "John Doe", "john@example.com"));
        booking.setItem(new Item(1L, "Test Item", "Test Description", true, owner,
                null));
        booking.setStatus(Status.APPROVED);
        booking.setStart(LocalDateTime.now().minusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(1));
    }

    @Test
    void mapToBookingDto_whenBookingIsProvided_thenBookingDtoIsReturned() {
        BookingDto dto = BookingMapper.mapToBookingDto(booking);

        assertNotNull(dto);
        assertEquals(booking.getId(), dto.getId());
        assertEquals(booking.getBooker(), dto.getBooker());
        assertEquals(booking.getItem(), dto.getItem());
        assertEquals(booking.getStatus(), dto.getStatus());
        assertEquals(booking.getStart(), dto.getStart());
        assertEquals(booking.getEnd(), dto.getEnd());
        assertEquals(State.CURRENT, dto.getState());
    }

    @Test
    void mapToItemBookingDto_whenBookingIsProvided_thenBookingInItemDtoIsReturned() {
        BookingInItemDto dto = BookingMapper.mapToItemBookingDto(booking);

        assertNotNull(dto);
        assertEquals(booking.getId(), dto.getId());
        assertEquals(booking.getBooker().getId(), dto.getBookerId());
        assertEquals(booking.getStatus(), dto.getStatus());
        assertEquals(booking.getStart(), dto.getStart());
        assertEquals(booking.getEnd(), dto.getEnd());
        assertEquals(State.CURRENT, dto.getState());
    }

    @Test
    void mapToBookingFromAddDto_whenBookingAddDtoIsProvided_thenBookingIsReturned() {
        BookingAddDto dto = new BookingAddDto();
        dto.setBooker(booking.getBooker());
        dto.setItemId(1L);
        dto.setStatus(Status.WAITING);
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));

        when(itemService.getItem(dto.getItemId())).thenReturn(booking.getItem());

        Booking mappedBooking = BookingMapper.mapToBookingFromAddDto(dto, itemService);

        assertNotNull(mappedBooking);
        assertEquals(dto.getBooker(), mappedBooking.getBooker());
        assertEquals(booking.getItem(), mappedBooking.getItem());
        assertEquals(dto.getStatus(), mappedBooking.getStatus());
        assertEquals(dto.getStart(), mappedBooking.getStart());
        assertEquals(dto.getEnd(), mappedBooking.getEnd());
    }
}
