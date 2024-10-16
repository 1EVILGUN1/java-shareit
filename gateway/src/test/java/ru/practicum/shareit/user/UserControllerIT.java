package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.client.UserClient;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerIT {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserClient userClient;

    @SneakyThrows
    @Test
    void getAllUsers_whenInvoked_ListOfUsersReturnedAndStatusIsOk() {
        UserDto userDto = new UserDto(1L, "name", "email@email.ru");
        when(userClient.getAllUsers()).thenReturn(ResponseEntity.ok(List.of(userDto)));

        mockMvc.perform(get("/users"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("name"))
                .andExpect(jsonPath("$[0].email").value("email@email.ru"));
    }

    @SneakyThrows
    @Test
    void saveNewUser_whenInvokedAndEmailIsUnique_thenUserReturnedAndStatusIsOk() {
        UserDto userDto = new UserDto(1L, "name", "email@mail.ru");
        when(userClient.save(any(UserDto.class))).thenReturn(ResponseEntity.ok(userDto));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("name"))
                .andExpect(jsonPath("$.email").value("email@mail.ru"));
    }

    @SneakyThrows
    @Test
    void saveNewUser_whenNameIsBlank_thenReturnStatusIsBadRequest() {
        UserDto userDto = new UserDto(1L, "", "email@mail.ru"); // Имя пустое

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @SneakyThrows
    @Test
    void saveNewUser_whenEmailIsInvalid_thenReturnStatusIsBadRequest() {
        UserDto userDto = new UserDto(1L, "name", "invalid-email"); // Неверный email

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void findById_whenUserFound_thenReturnStatusIsOkAndUserDto() {
        long userId = 1L;
        UserDto userDto = new UserDto(1L, "name", "email@email.ru");
        when(userClient.findById(userId)).thenReturn(ResponseEntity.ok(userDto));

        mockMvc.perform(get("/users/{id}", userId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("name"))
                .andExpect(jsonPath("$.email").value("email@email.ru"));
    }

    @SneakyThrows
    @Test
    void findById_whenUserIdIsNegative_thenReturnStatusIsInternalServerError() {
        long userId = -1L;

        mockMvc.perform(get("/users/{id}", userId))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @SneakyThrows
    @Test
    void updateUser_whenUserFound_thenUserReturnedAndStatusIsOk() {
        long userId = 1L;
        UserDto userDto = new UserDto(1L, "updatedName", "updatedEmail@email.ru");
        when(userClient.update(eq(userId), any(UserDto.class))).thenReturn(ResponseEntity.ok(userDto));

        mockMvc.perform(patch("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("updatedName"))
                .andExpect(jsonPath("$.email").value("updatedEmail@email.ru"));
    }

    @SneakyThrows
    @Test
    void updateUser_whenUserIdIsNegative_thenReturnStatusIsInternalServerError() {
        long userId = -1L;
        UserDto userDto = new UserDto(1L, "updatedName", "updatedEmail@email.ru");

        mockMvc.perform(patch("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }

    @SneakyThrows
    @Test
    void deleteUser_whenInvoked_thenStatusIsOk() {
        long userId = 1L;
        when(userClient.delete(userId)).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(delete("/users/{id}", userId))
                .andDo(print())
                .andExpect(status().isOk());

        verify(userClient).delete(userId);
    }
}