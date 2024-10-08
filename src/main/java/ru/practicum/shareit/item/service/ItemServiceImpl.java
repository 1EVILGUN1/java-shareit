package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.ItemDoNotBelongToUser;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.validators.ItemValidator;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.validators.UserValidator;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final UserService userService;
    private final ItemRepository repository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional
    public Item save(Long userId, ItemDto dto) {
        UserValidator.validateId(userId);
        log.info("Проверка на наличие пользователя с id: {} ", userId);
        User user = userService.findUserById(userId);
        log.info("Сохраняем предмет с id пользователя: {} ", userId);
        Item item = ItemMapper.mapToItem(dto);
        item.setOwner(user);

        return repository.save(item);
    }

    @Override
    @Transactional
    public Item update(Long userId, Long itemId, ItemDto dto) {
        UserValidator.validateId(userId);
        ItemValidator.validateId(itemId);
        log.info("Проверка на наличие пользователя с id: {} ", userId);
        userService.findUserById(userId);
        log.info("Обновление предмета с id: {} ", itemId);

        Item item = repository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Предмет с id = " + itemId + " не найден"));
        if (!item.getOwner().getId().equals(userId)) {
            throw new ItemDoNotBelongToUser("Предмет с id = " + itemId + " не принадлежит пользователю с id = " +
                                            userId);
        }
        if (dto.getName() != null) {
            item.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            item.setDescription(dto.getDescription());
        }
        if (dto.isAvailable() != null) {
            item.setAvailable(dto.isAvailable());
        }
        return repository.save(item);
    }

    @Override
    public Item findItemById(Long itemId) {
        ItemValidator.validateId(itemId);
        return repository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Предмет с id = " + itemId + " не найден"));
    }

    @Override
    public List<Item> getAllItemsOfOwner(Long userId) {
        UserValidator.validateId(userId);
        return repository.findByOwnerId(userId);
    }

    @Override
    public List<Item> searchItems(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        return repository.search(text)
                .stream()
                .filter(Item::isAvailable)
                .toList();
    }

    @Override
    @Transactional
    public Comment addComment(Long authorId, Long itemId, CommentDto dto) {
        log.info("Добавление комментария для предмета с id= " + itemId);
        UserValidator.validateId(authorId);
        ItemValidator.validateId(itemId);
        Booking booking = bookingRepository.findByItemIdAndBookerIdAndEndBefore(itemId, authorId, LocalDateTime.now())
                .orElseThrow(() -> new ValidationException("Бронь с указанными параметрами не существует>"));
        User booker = booking.getBooker();
        Comment comment = new Comment();
        comment.setItem(booking.getItem());
        comment.setAuthor(booker);
        comment.setText(dto.getText());
        comment.setCreated(LocalDateTime.now());
        return commentRepository.save(comment);
    }

    @Override
    public List<Comment> getComments(Long itemId) {
        return commentRepository.findAllByItemId(itemId);
    }
}
