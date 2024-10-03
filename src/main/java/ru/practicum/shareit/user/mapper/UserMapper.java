package ru.practicum.shareit.user.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;

@Component
public final class UserMapper {
    public static UserDto mapToUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static User mapUserCreatedDtoToUser(UserCreateDto userCreateDto) {
        return User.builder()
                .email(userCreateDto.getEmail())
                .name(userCreateDto.getName())
                .build();
    }

    public static User mapUserUpdatedDtoToUser(UserUpdateDto userUpdateDto, long userId) {
        return User.builder()
                .id(userId)
                .email(userUpdateDto.getEmail())
                .name(userUpdateDto.getName())
                .build();
    }

    public static User mapUserDtoToUser(UserDto userDto) {
        return User.builder()
                .id(userDto.getId())
                .email(userDto.getEmail())
                .name(userDto.getName())
                .build();
    }
}
