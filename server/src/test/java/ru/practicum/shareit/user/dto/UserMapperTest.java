package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserMapperTest {

    @Test
    void mapToDto_whenUserIsProvided_thenUserDtoIsReturned() {
        User user = new User(1L, "Test User", "test@example.com");

        UserDto userDto = UserMapper.mapToDto(user);

        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getEmail(), userDto.getEmail());
    }

    @Test
    void mapToUser_whenUserDtoIsProvided_thenUserIsReturned() {
        UserDto userDto = new UserDto(1L, "Test User", "test@example.com");

        User user = UserMapper.mapToUser(userDto);

        assertEquals(userDto.getId(), user.getId());
        assertEquals(userDto.getName(), user.getName());
        assertEquals(userDto.getEmail(), user.getEmail());
    }
}
