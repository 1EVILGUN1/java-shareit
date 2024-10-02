package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final UserService userService;
    private final CommentRepository commentRepository;
    private final ItemRepository repository;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional
    public ItemDto save(long userId, ItemDto itemDto) {
        Item item = ItemMapper.mapToItem(itemDto);
        item.setUser(UserMapper.mapToUser(userService.findById(userId)));
        Item savedItem = repository.save(item);
        return ItemMapper.mapToItemDto(savedItem);
    }

    @Override
    @Transactional
    public ItemDto update(long userId, long itemId, ItemDto itemDto) {
        Item itemDb = ItemMapper.mapToItem(findById(itemId, userId));
        Item item = ItemMapper.mapToItem(itemDto);
        if (!itemDb.getUser().getId().equals(userId)) {
            throw new NotFoundException(
                    String.format("Пользователь с ID: %d не владеет вещью с ID: %d", userId, itemId));
        }
        itemDb.setAvailable(item.getAvailable() != null ? item.getAvailable() : itemDb.getAvailable());
        itemDb.setDescription(item.getDescription() != null ? item.getDescription() : itemDb.getDescription());
        itemDb.setName(item.getName() != null ? item.getName() : itemDb.getName());
        Item updatedItem = repository.save(itemDb);
        return ItemMapper.mapToItemDto(updatedItem);
    }

    @Override
    public ItemDto findById(long itemId, long userId) {
        userService.findById(userId);
        Item item = checkItem(itemId);
        return lastAndNextBooking(item, userId);
    }

    @Override
    public List<ItemDto> getItems(long userId) {
        userService.findById(userId);
        List<Item> items = repository.findAllByUserId(userId);
        List<ItemDto> itemDtos = new ArrayList<>();
        for (Item item : items) {
            itemDtos.add(lastAndNextBooking(item, userId));
        }
        return itemDtos;
    }

    @Override
    @Transactional
    public ItemDto delete(long userId, long itemId) {
        userService.findById(userId);
        Item item = checkItem(itemId);
        repository.deleteById(itemId);
        return ItemMapper.mapToItemDto(item);
    }

    @Override
    public Collection<ItemDto> searchItemByName(String text, long userId) {
        userService.findById(userId);
        if (text.isBlank()) {
            return List.of();
        }
        return ItemMapper.mapToListItemDto(
                repository.findAllByNameOrDescriptionContainingIgnoreCaseAndAvailableTrue(text, text));
    }

    @Override
    public CommentDto saveComment(CommentDto commentDto, long itemId, long userId) {
        Comment comment = CommentMapper.mapToComment(commentDto);
        Item item = checkItem(itemId);
        User user = UserMapper.mapToUser(userService.findById(userId));
        List<Booking> bookings = bookingRepository.findByItemIdAndBookerId(itemId, userId);
        if (bookings.isEmpty()) {
            throw new BadRequestException("Только арендаторы могут оставлять отзыв!");
        }
        for (Booking booking : bookings) {
            if (Status.REJECTED == booking.getStatus()) {
                throw new BadRequestException("Нельзя оставить отзыв, есть аренда невозможна!");
            }
        }
        Optional<Booking> booking = bookings.stream().min(Comparator.comparing(Booking::getStart));
        if (booking.get().getStart().isAfter(comment.getCreated())) {
            throw new BadRequestException("Нельзя оставлять отзыв до аренды!");
        }
        comment.setItem(item);
        comment.setAuthor(user);
        return CommentMapper.mapToCommentDto(commentRepository.save(comment));
    }

    private Item checkItem(long itemId) {
        return repository.findById(itemId).orElseThrow(() ->
                new NotFoundException(String.format("Вещь с ID %d не найдена", itemId)));
    }

    private ItemDto lastAndNextBooking(Item item, long userId) {
        List<Booking> lastBookings = bookingRepository.findAllByItemIdAndEndBeforeOrderByEndDesc(
                item.getId(), LocalDateTime.now());
        List<Booking> nextBookings = bookingRepository.findAllByItemIdAndStartAfter(item.getId(), LocalDateTime.now());
        ItemDto itemDto = ItemMapper.mapToItemDto(item);
        if (item.getUser().getId().equals(userId)) {
            if (!lastBookings.isEmpty() && !nextBookings.isEmpty()) {
                Booking lastBooking = lastBookings.get(0);
                Booking nextBooking = nextBookings.get(nextBookings.size() - 1);
                itemDto.setLastBooking(BookingMapper.mapToBookingDto(lastBooking));
                itemDto.setNextBooking(BookingMapper.mapToBookingDto(nextBooking));
            } else {
                List<Booking> activeBookings =
                        bookingRepository.findAllByEndAfterAndStartBeforeAndItemUserIdOrderByStartDesc(
                                LocalDateTime.now(), LocalDateTime.now(), userId);
                for (Booking booking : activeBookings) {
                    if (booking.getItem().getId().equals(item.getId())) {
                        itemDto.setLastBooking(BookingMapper.mapToBookingDto(booking));
                    }
                }
            }
        }
        itemDto.setComments(CommentMapper.mapToListCommentDto(
                commentRepository.findAllByItemId(item.getId())));
        return itemDto;
    }
}
