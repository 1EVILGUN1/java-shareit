package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository repository;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;


    @Override
    @Transactional
    public ItemDto save(long userId, ItemCreateDto itemCreateDto) {
        Item item = ItemMapper.mapItemCreatedDtoToItem(itemCreateDto);
        item.setUser(UserMapper.mapUserDtoToUser(userService.findById(userId)));
        Item savedItem = repository.save(item);
        return ItemMapper.mapToItemDto(savedItem);
    }

    @Override
    @Transactional
    public ItemDto update(long userId, long itemId, ItemUpdateDto itemUpdateDto) {
        Item itemFromDb = checkItemDto(itemId);
        Item item = ItemMapper.mapItemUpdatedDtoToItem(itemUpdateDto);
        if (!itemFromDb.getUser().getId().equals(userId)) {
            throw new NotFoundException(
                    String.format("Пользователь с ID: %d не владеет вещью с ID: %d", userId, itemId));
        }
        itemFromDb.setName(item.getName() != null ? item.getName() : itemFromDb.getName());
        itemFromDb.setDescription(item.getDescription() != null ? item.getDescription() : itemFromDb.getDescription());
        itemFromDb.setAvailable(item.getAvailable() != null ? item.getAvailable() : itemFromDb.getAvailable());
        Item updatedItem = repository.save(itemFromDb);
        return ItemMapper.mapToItemDto(updatedItem);
    }

    @Override
    public ItemDto findById(long itemId, long userId) {
        userService.checkUserDto(userId);
        Item item = checkItemDto(itemId);
        return lastAndNextBookings(item, userId);
    }

    @Override
    public Collection<ItemDto> getItems(long userId) {
        userService.checkUserDto(userId);
        List<Item> items = repository.findAllByUserId(userId);
        List<ItemDto> itemDtos = new ArrayList<>();
        for (Item item : items) {
            itemDtos.add(lastAndNextBookings(item, userId));
        }
        return itemDtos;
    }

    @Override
    @Transactional
    public ItemDto delete(long userId, long itemId) {
        Item item = checkItemDto(itemId);
        repository.deleteById(itemId);
        return ItemMapper.mapToItemDto(item);
    }

    @Override
    public Collection<ItemDto> searchItemByName(String text, long userId) {
        userService.checkUserDto(userId);
        if (text.isBlank()) {
            return List.of();
        }
        List<Item> items = repository.findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableTrue(text, text).stream()
                .filter(Item::getAvailable)
                .collect(Collectors.toList());
        return ItemMapper.mapToItemDtoList(items);

    }

    @Override
    public Item checkItemDto(long id) {
        return repository.findById(id).orElseThrow(() ->
                new NotFoundException(String.format("Вещь с ID %d не найдена", id)));
    }

    @Override
    @Transactional
    public CommentDto addComment(CommentCreateDto commentCreateDto, long itemId, long userId) {
        Comment comment = CommentMapper.mapCommentCreatedDtoToComment(commentCreateDto);
        Item item = checkItemDto(itemId);
        User user = userService.checkUserDto(userId);
        List<Booking> bookings = bookingRepository.findByItemIdAndBookerId(itemId, userId);
        if (bookings.isEmpty()) {
            throw new BadRequestException("Только арендаторы могут оставлять отзыв!");
        }
        if (bookings.stream().anyMatch(b -> Status.REJECTED == b.getStatus())) {
            throw new BadRequestException("Нельзя оставить отзыв, аренда невозможна!");
        }
        Booking earliestBooking = bookings.stream().min(Comparator.comparing(Booking::getStart)).orElseThrow();
        if (earliestBooking.getStart().isAfter(comment.getCreated())) {
            throw new BadRequestException("Нельзя оставлять отзыв до аренды!");
        }
        comment.setItem(item);
        comment.setAuthor(user);
        return CommentMapper.mapToCommentDto(commentRepository.save(comment));
    }

    private ItemDto lastAndNextBookings(Item item, long userId) {
        List<Booking> lastBookings = bookingRepository.findAllByItemIdAndEndBeforeOrderByEndDesc(item.getId(), LocalDateTime.now());
        List<Booking> nextBookings = bookingRepository.findAllByItemIdAndStartAfter(item.getId(), LocalDateTime.now());
        ItemDto itemDto = ItemMapper.mapToItemDto(item);

        if (item.getUser().getId().equals(userId)) {
            if (!lastBookings.isEmpty()) {
                itemDto.setLastBooking(BookingMapper.mapBookingToBookingItemDto(lastBookings.get(0)));
            }
            if (!nextBookings.isEmpty()) {
                itemDto.setNextBooking(BookingMapper.mapBookingToBookingItemDto(nextBookings.get(nextBookings.size() - 1)));
            } else {
                bookingRepository.findAllByEndAfterAndStartBeforeAndItemUserIdOrderByStartDesc(LocalDateTime.now(), LocalDateTime.now(), userId)
                        .stream()
                        .filter(booking -> booking.getItem().getId().equals(item.getId()))
                        .findFirst()
                        .ifPresent(booking -> itemDto.setLastBooking(BookingMapper.mapBookingToBookingItemDto(booking)));
            }
        }

        itemDto.setComments(CommentMapper.mapToCommentsDtoList(commentRepository.findAllByItemId(item.getId())));
        return itemDto;
    }
}
