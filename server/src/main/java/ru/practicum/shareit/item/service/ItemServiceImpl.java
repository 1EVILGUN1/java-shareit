package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ItemDoNotBelongToUser;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final UserService userService;
    private final ItemRepository repository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    @Transactional
    public Item save(Long userId, ItemDto dto) {
        log.debug("Check user with id: {} ", userId);
        User user = userService.findById(userId);
        Item item = ItemMapper.mapToItem(dto);
        if (dto.getRequestId() != null) {
            log.debug("Request item SPECIFIED, check for availability of request with id = " + dto.getRequestId() + " in DB");
            ItemRequest itemRequest = itemRequestRepository
                    .findById(dto.getRequestId())
                    .orElseThrow(() -> new NotFoundException("Request with id = " + dto.getRequestId() + " does not exist"));
            item.setRequest(itemRequest);
        }
        log.debug("Save item with id user: {} ", userId);
        item.setOwner(user);

        return repository.save(item);
    }

    @Override
    @Transactional
    public Item update(Long userId, Long itemId, ItemDto dto) {
        log.debug("Check user with id: {} ", userId);
        userService.findById(userId);
        log.debug("Update item with id: {} ", itemId);
        Item item = getItem(itemId);
        if (!item.getOwner().getId().equals(userId)) {
            throw new ItemDoNotBelongToUser("Item with id = " + itemId + " does not belong to user with id = " +
                                            userId);
        }
        item.setName(dto.getName() != null ? dto.getName() : item.getName());
        item.setDescription(dto.getDescription() != null ? dto.getDescription() : item.getDescription());
        item.setAvailable(dto.isAvailable() != null ? dto.isAvailable() : item.getAvailable());
        return repository.save(item);
    }

    @Override
    public Item getItem(Long itemId) {
        return repository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item with id = " + itemId + " does not exist"));
    }

    @Override
    public List<Item> getAllItemsOfOwner(Long userId) {
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
        log.debug("Add comment of item with id={}, authorId={}, dto={}", itemId, authorId, dto);
        userService.findById(authorId);
        repository.findById(itemId).orElseThrow(() -> new NotFoundException("Item does not exist with id=" + itemId));
        Booking booking = bookingRepository.findByItemIdAndBookerIdAndEndBefore(itemId, authorId, LocalDateTime.now())
                .orElseThrow(() -> new ValidationException("There is no reservation with the specified parameters - " +
                                                           "when adding a comment"));
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

    @Override
    public List<Item> getItemsByRequestId(Long requestId) {
        return repository.findAllByRequestId(requestId);
    }
}
