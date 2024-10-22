package ru.practicum.shareit.request.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository repository;
    private final UserService userService;

    @Override
    @Transactional
    public ItemRequest addNewRequest(Long requestorId, ItemRequest itemRequest) {
        log.debug("Check user with id = {} in DB on add request", requestorId);
        User requestor = userService.findById(requestorId);
        itemRequest.setRequestor(requestor);
        log.debug("An attempt to write a query to the DB when adding a query for an item");
        return repository.save(itemRequest);
    }

    @Override
    public List<ItemRequest> getRequestsOfRequestor(Long requestorId) {
        log.debug("Check user when searching your requests with id = {} in DB", requestorId);
        userService.findById(requestorId);
        return repository.findByRequestorIdOrderByCreatedDesc(requestorId);
    }

    @Override
    public List<ItemRequest> getAllRequestsOfOtherUsers(Long userId) {
        log.debug("Check user when searching stranger requests with id = {} in DB", userId);
        userService.findById(userId);
        return repository.findAllExcludedByUserIdDesc(userId);
    }

    @Override
    public ItemRequest getRequestById(Long userId, Long requestId) {
        log.debug("Check user when searching specific request with id = {} in DB", userId);
        userService.findById(userId);
        log.debug("User userId={}", userId);
        log.debug("Request requestId={}", requestId);
        return repository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request with id = " + requestId + " doesn't exists"));
    }
}
