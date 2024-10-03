package ru.practicum.shareit.item.dto;

import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class CommentCreateDto {
    @NotBlank(message = "Текст не может быть пустым!")
    String text;
}
