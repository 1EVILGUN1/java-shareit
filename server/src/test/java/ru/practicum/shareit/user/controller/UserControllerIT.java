package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.EmailDoubleException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
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
    UserService userService;

    @SneakyThrows
    @Test
    void getAllUsers_whenInvoked_ListOfUsersReturnedAndStatusIsOk() {
        User user = new User(1L, "name", "email@email.ru");
        when(userService.findAllUsers()).thenReturn(List.of(user));

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
        User user = new User(1L, "name", "email@mail.ru");
        when(userService.save(any(User.class))).thenReturn(user);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("name"))
                .andExpect(jsonPath("$.email").value("email@mail.ru"));
    }

    @SneakyThrows
    @Test
    void saveNewUser_whenInvokedAndEmailNotUnique_EmailDoubleExceptionThrownAndStatusIsConflict() {
        User user = new User(1L, "name", "email@mail.ru");
        when(userService.save(any(User.class))).thenThrow(new EmailDoubleException("Email должен быть уникальным"));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect((jsonPath("$.error").value("Повтор email")))
                .andExpect((jsonPath("$.description").value("Email должен быть уникальным")));
    }

    @SneakyThrows
    @Test
    void findById_whenUserFound_thenReturnStatusIsOkAndUserDto() {
        long userId = 1L;
        UserDto userDto = new UserDto(1L, "name", "email@email.ru");
        User user = new User(1L, "name", "email@email.ru");
        when(userService.findById(1L)).thenReturn(user);

        String result = mockMvc.perform(get("/users/{id}", userId))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(userDto), result);
    }

    @SneakyThrows
    @Test
    void findById_whenUserNotFound_thenReturnStatusIsNotFoundAndNotFoundExceptionThrown() {
        long userId = 1L;
        when(userService.findById(1L)).thenThrow(new NotFoundException("Пользователь с таким id = " + userId +
                                                                       " не существует"));

        mockMvc.perform(get("/users/{id}", userId))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Ошибка с входными параметрами."))
                .andExpect(jsonPath("$.description").value("Пользователь с таким id = "
                                                           + userId + " не существует"));
    }

    @SneakyThrows
    @Test
    void update_whenUserFound_thenUserReturnedAndStatusIsOk() {
        User user = new User(1L, "updatedName", "updatedEmail@email.ru");
        when(userService.update(anyLong(), any(User.class))).thenReturn(user);

        mockMvc.perform(patch("/users/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("updatedName"))
                .andExpect(jsonPath("$.email").value("updatedEmail@email.ru"));
    }

    @SneakyThrows
    @Test
    void update_whenUserNotFound_thenNotFoundExceptionThrownAndStatusIsNotFound() {
        long userId = 1L;
        User user = new User(1L, "updatedName", "updatedEmail@email.ru");
        when(userService.update(anyLong(), any(User.class)))
                .thenThrow(new NotFoundException("Пользователь с таким id = "
                                                 + userId + " не существует"));

        mockMvc.perform(patch("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Ошибка с входными параметрами."))
                .andExpect(jsonPath("$.description").value("Пользователь с таким id = "
                                                           + userId + " не существует"));
    }

    @SneakyThrows
    @Test
    void update_whenUserFoundAndEmailIsNotUnique_thenEmailDoubleExceptionThrownAndStatusIsConflict() {
        long userId = 1L;
        User user = new User(1L, "updatedName", "updatedEmail@email.ru");
        when(userService.update(anyLong(), any(User.class)))
                .thenThrow(new EmailDoubleException("Email должен быть уникальным"));

        mockMvc.perform(patch("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Повтор email"))
                .andExpect(jsonPath("$.description").value("Email должен быть уникальным"));
    }


    @SneakyThrows
    @Test
    void delete_whenInvoked_thenStatusIsOk() {
        long userId = 1L;
        doNothing().when(userService).delete(anyLong());

        mockMvc.perform(delete("/users/{id}", userId))
                .andExpect(status().isOk());

        verify(userService).delete(userId);
    }
}