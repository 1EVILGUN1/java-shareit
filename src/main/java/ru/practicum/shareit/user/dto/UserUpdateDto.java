package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import lombok.Getter;



@Getter
public class UserUpdateDto {
    @Email(message = "Введён некорректный e-mail")
    private String email;
    private String name;
}
