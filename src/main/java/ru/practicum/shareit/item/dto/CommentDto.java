package ru.practicum.shareit.item.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CommentDto {
    Long id;
    @NotBlank(message = "Текст не может быть пустым!")
    String text;
    String authorName;
    LocalDateTime created;
}
