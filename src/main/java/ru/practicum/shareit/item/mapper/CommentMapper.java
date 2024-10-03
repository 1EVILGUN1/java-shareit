package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public final class CommentMapper {
    public static CommentDto mapToCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }

    public static Comment mapCommentCreatedDtoToComment(CommentCreateDto commentCreateDto) {
        return Comment.builder()
                .text(commentCreateDto.getText())
                .created(LocalDateTime.now())
                .build();
    }

    public static List<CommentDto> mapToCommentsDtoList(List<Comment> comments) {
        return comments.stream().map(CommentMapper::mapToCommentDto).collect(Collectors.toList());
    }
}
