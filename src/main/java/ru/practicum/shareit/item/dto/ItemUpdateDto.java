package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;


@Getter
public class ItemUpdateDto {
    private Long id;
    @NotBlank(message = "Имя не может быть пустым!")
    private String name;
    @NotBlank(message = "Описание не может быть пустым!")
    private String description;
    @NotBlank(message = "Доступ аренды не указан!")
    private Boolean available;
}
