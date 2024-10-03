package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ItemCreateDto {
    @NotBlank(message = "Имя не может быть пустым!")
    private String name;
    @NotBlank(message = "Описание не может быть пустым!")
    private String description;
    @NotNull(message = "Доступ аренды не указан!")
    private Boolean available;
}
