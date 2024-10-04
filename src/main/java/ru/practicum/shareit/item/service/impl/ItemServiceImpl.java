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
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;


    @Override
    @Transactional
    public ItemDto create(long userId, ItemCreatedDto itemCreatedDto) {
        Item item = ItemMapper.mapItemCreatedDtoToItem(itemCreatedDto);
        item.setUser(userService.checkUserDto(userId));
        return ItemMapper.mapToItemDto(itemRepository.save(item));
    }

    @Override
    @Transactional
    public ItemDto update(long userId, long itemId, ItemUpdatedDto itemUpdatedDto) {
        Item itemFromDb = checkItemDto(itemId);
        Item item = ItemMapper.mapItemUpdatedDtoToItem(itemUpdatedDto);
        if (!itemFromDb.getUser().getId().equals(userId)) {
            throw new NotFoundException(
                    String.format("Пользователь с ID: %d не владеет вещью с ID: %d", userId, itemId));
        }
        itemFromDb.setAvailable(item.getAvailable() != null ? item.getAvailable() : itemFromDb.getAvailable());
        itemFromDb.setDescription(item.getDescription() != null ? item.getDescription() : itemFromDb.getDescription());
        itemFromDb.setName(item.getName() != null ? item.getName() : itemFromDb.getName());
        itemRepository.save(itemFromDb);
        return ItemMapper.mapToItemDto(itemFromDb);
    }

    @Override
    public ItemDto getItemById(long itemId, long userId) {
        userService.checkUserDto(userId);
        Item item = checkItemDto(itemId);
        return getLastAndNextBookings(item, userId);
    }

    @Override
    public Collection<ItemDto> getItems(long userId) {
        userService.checkUserDto(userId);
        List<Item> items = itemRepository.findAllByUserId(userId);
        List<ItemDto> itemDtos = new ArrayList<>();
        for (Item item : items) {
            itemDtos.add(getLastAndNextBookings(item, userId));
        }
        return itemDtos;
    }

    @Override
    @Transactional
    public ItemDto remove(long userId, long itemId) {
        Item item = checkItemDto(itemId);
        itemRepository.deleteById(itemId);
        return ItemMapper.mapToItemDto(item);
    }

    @Override
    public Collection<ItemDto> searchItemByName(String text, long userId) {
        userService.checkUserDto(userId);
        if (text.isBlank()) {
            return List.of();
        }

        return itemRepository.findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableTrue(text, text).stream()
                .filter(Item::getAvailable)
                .map(ItemMapper::mapToItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public Item checkItemDto(long id) {
        return itemRepository.findById(id).orElseThrow(() ->
                new NotFoundException(String.format("Вещь с ID %d не найдена", id)));
    }

    @Override
    @Transactional
    public CommentDto addComment(CommentCreatedDto commentCreatedDto, long itemId, long userId) {
        Comment comment = CommentMapper.mapCommentCreatedDtoToComment(commentCreatedDto);
        Item item = checkItemDto(itemId);
        User user = userService.checkUserDto(userId);
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

    private ItemDto getLastAndNextBookings(Item item, long userId) {
        List<Booking> lastBookings = bookingRepository.findAllByItemIdAndEndBeforeOrderByEndDesc(
                item.getId(), LocalDateTime.now());
        List<Booking> nextBookings = bookingRepository.findAllByItemIdAndStartAfter(item.getId(), LocalDateTime.now());
        ItemDto itemDto = ItemMapper.mapToItemDto(item);
        if (item.getUser().getId().equals(userId)) {
            if (!lastBookings.isEmpty() && !nextBookings.isEmpty()) {
                Booking lastBooking = lastBookings.get(0);
                Booking nextBooking = nextBookings.get(nextBookings.size() - 1);
                itemDto.setLastBooking(BookingMapper.mapBookingToBookingItemDto(lastBooking));
                itemDto.setNextBooking(BookingMapper.mapBookingToBookingItemDto(nextBooking));
            } else {
                List<Booking> activeBookings =
                        bookingRepository.findAllByEndAfterAndStartBeforeAndItemUserIdOrderByStartDesc(
                                LocalDateTime.now(), LocalDateTime.now(), userId);
                for (Booking booking : activeBookings) {
                    if (booking.getItem().getId().equals(item.getId())) {
                        itemDto.setLastBooking(BookingMapper.mapBookingToBookingItemDto(booking));
                    }
                }
            }
        }
        itemDto.setComments(CommentMapper.mapToCommentsDtoList(
                commentRepository.findAllByItemId(item.getId())));
        return itemDto;
    }
}
