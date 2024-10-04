package ru.practicum.shareit.item.dto;

import lombok.Getter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Getter
public class ItemCreatedDto {
    @NotBlank(message = "Имя не может быть пустым!")
    private String name;
    @NotBlank(message = "Описание не может быть пустым!")
    private String description;
    @NotNull(message = "Доступ аренды не указан!")
    private Boolean available;
}
