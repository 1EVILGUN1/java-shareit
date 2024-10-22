package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.validators.UserValidator;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Override
    public List<User> findAllUsers() {
        return repository.findAll();
    }

    @Override
    @Transactional
    public User save(User user) {
        UserValidator.checkEmailIsUnique(repository.findUserByEmail(user.getEmail()));
        log.debug("Create user: {} ", user.getName());
        return repository.save(user);
    }

    @Override
    @Transactional
    public User update(Long id, User updatedUser) {
        log.debug("Update user with id: {} ", id);
        User user = findById(id);
        updatedUser.setId(id);
        if (updatedUser.getEmail() != null) {
            UserValidator.checkEmailIsUnique(repository.findUserByEmail(updatedUser.getEmail()));
        } else {
            updatedUser.setEmail(user.getEmail());
        }
        if (updatedUser.getName() == null) {
            updatedUser.setName(user.getName());
        }
        return repository.save(updatedUser);
    }

    @Override
    public User findById(Long id) {
        log.debug("Search user with id={}", id);
        return repository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("User with id = " + id + " doesn't exist"));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
