package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.validators.UserValidator;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Override
    public List<User> findAllUsers() {
        return repository.findAll();
    }

    @Override
    public User save(User user) {
        UserValidator.checkEmailIsUnique(repository.findUserByEmail(user.getEmail()));
        log.info("Создание пользователя: {} ", user.getName());
        return repository.save(user);
    }

    @Override
    public User update(Long id, User updatedUser) {
        UserValidator.validateId(id);
        log.info("Обновление пользователя с id: {} ", id);
        User user = repository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с таким id = "
                                                         + updatedUser.getId() + " не существует"));
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
    public User findUserById(Long id) {
        return repository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с таким id = " + id + " не существует"));
    }

    @Override
    public User delete(Long id) {
        User user = findUserById(id);
        repository.deleteById(id);
        return user;
    }
}
