package ru.practicum.shareit.user.dto;

import lombok.Getter;

import javax.validation.constraints.Email;

@Getter
public class UserUpdateDto {
    @Email(message = "Введён некорректный e-mail")
    private String email;
    private String name;
}
