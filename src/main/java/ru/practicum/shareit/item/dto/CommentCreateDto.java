package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;



@Getter
public class CommentCreateDto {
    @NotBlank(message = "Текст не может быть пустым!")
    String text;
}
