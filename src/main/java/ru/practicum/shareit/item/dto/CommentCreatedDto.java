package ru.practicum.shareit.item.dto;

import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class CommentCreatedDto {
    @NotBlank(message = "Текст не может быть пустым!")
    String text;
}
