package ru.practicum.shareit.error;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.EmailDoubleException;
import ru.practicum.shareit.exception.ItemDoNotBelongToUser;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ErrorHandlerTest {

    private ErrorHandler errorHandler;

    @BeforeEach
    void setUp() {
        errorHandler = new ErrorHandler();
    }

    @Test
    void handleValidationException_ShouldReturnBadRequest() {
        String message = "Error validate date";
        ValidationException exception = Mockito.mock(ValidationException.class);
        Mockito.when(exception.getMessage()).thenReturn(message);

        ErrorResponse response = errorHandler.handleValidationException(exception);

        assertEquals("Error validate date", response.error());
        assertEquals(message, response.description());
    }

    @Test
    void handleNotFoundException_ShouldReturnNotFound() {
        String message = "Error with input parameters";
        NotFoundException exception = Mockito.mock(NotFoundException.class);
        Mockito.when(exception.getMessage()).thenReturn(message);

        ErrorResponse response = errorHandler.handleNotFoundException(exception);

        assertEquals("Error with input parameters", response.error());
        assertEquals(message, response.description());
    }

    @Test
    void handleThrowable_ShouldReturnInternalServerError() {
        String message = "An error occurred.";
        Throwable exception = Mockito.mock(Throwable.class);
        Mockito.when(exception.getMessage()).thenReturn(message);

        ErrorResponse response = errorHandler.handleThrowable(exception);

        assertEquals("An error occurred.", response.error());
        assertEquals(message, response.description());
    }

    @Test
    void handleEmailDoubleException_ShouldReturnConflict() {
        String message = "Error with input parameters (repeat email)";
        EmailDoubleException exception = Mockito.mock(EmailDoubleException.class);
        Mockito.when(exception.getMessage()).thenReturn(message);

        ErrorResponse response = errorHandler.handleEmailDoubleException(exception);

        assertEquals("Error with input parameters (repeat email)", response.error());
        assertEquals(message, response.description());
    }

    @Test
    void handleItemDoNotBelongToUser_ShouldReturnForbidden() {
        String message = "Error - Access is denied for the current user";
        ItemDoNotBelongToUser exception = Mockito.mock(ItemDoNotBelongToUser.class);
        Mockito.when(exception.getMessage()).thenReturn(message);

        ErrorResponse response = errorHandler.handleItemDoNotBelongToUser(exception);

        assertEquals("Error - Access is denied for the current user", response.error());
        assertEquals(message, response.description());
    }
}
