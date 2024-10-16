package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CommentMapperTest {

    @Test
    void mapToCommentDto_whenCommentIsProvided_thenCommentDtoIsReturned() {
        User author = new User(1L, "Author Name", "author@example.com");
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setAuthor(author);
        comment.setText("This is a test comment.");
        comment.setCreated(LocalDateTime.now());

        CommentDto commentDto = CommentMapper.mapToCommentDto(comment);

        assertEquals(comment.getId(), commentDto.getId());
        assertEquals(comment.getText(), commentDto.getText());
        assertEquals(comment.getCreated(), commentDto.getCreated());
        assertEquals(author.getName(), commentDto.getAuthorName());
    }
}
