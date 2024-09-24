package ru.practicum.shareit.item.controller.mapper.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@NotNull
public class ItemDto {
    private Long id;
    @NotBlank(message = "Имя не может быть пустым!")
    @NotNull(message = "Имя не может быть пустым!")
    private String name;
    @NotNull(message = "Описание не может быть пустым!")
    private String description;
    @NotNull(message = "Статус для аренды не может быть пустым!")
    private Boolean available;
}
