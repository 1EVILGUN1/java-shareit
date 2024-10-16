package ru.practicum.shareit.item.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {

    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> save(long userId, ItemDto dto) {
        return post("", userId, dto);
    }

    public ResponseEntity<Object> update(long userId, long id, ItemDto dto) {
        return patch("/" + id, userId, dto);
    }

    public ResponseEntity<Object> findById(long userId, long id) {
        return get("/" + id, userId);
    }

    public ResponseEntity<Object> getItemsOfOwner(long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> searchItems(long userId, String text) {
        Map<String, Object> params = Map.of(
                "text", text
        );
        return get("/search", userId, params);
    }

    public ResponseEntity<Object> addComment(long authorId, long itemId, CommentDto dto) {
        return post("/" + itemId + "/comment", authorId, dto);
    }
}
