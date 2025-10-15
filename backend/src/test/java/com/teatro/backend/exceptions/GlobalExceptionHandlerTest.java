package com.teatro.backend.exceptions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void shouldHandleResourceNotFound() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Recurso no encontrado");
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = handler.handleResourceNotFound(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Recurso no encontrado", response.getBody().message());
        assertNotNull(response.getBody().timestamp());
    }

    @Test
    void shouldHandleInsufficientCapacity() {
        InsufficientCapacityException ex = new InsufficientCapacityException("No hay capacidad disponible");
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = handler.handleInsufficientCapacity(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("No hay capacidad disponible", response.getBody().message());
    }

    @Test
    void shouldHandleIllegalState() {
        IllegalStateException ex = new IllegalStateException("Estado inválido");
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = handler.handleIllegalState(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Estado inválido", response.getBody().message());
    }

    @Test
    void shouldHandleValidationErrors() {
        // Simulamos un error de validación en el campo "title"
        BeanPropertyBindingResult bindingResult =
                new BeanPropertyBindingResult(new Object(), "eventDTO");
        bindingResult.addError(new FieldError("eventDTO", "title", "Title is required"));

        MethodArgumentNotValidException ex =
                new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<Map<String, String>> response = handler.handleValidation(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().containsKey("title"));
        assertEquals("Title is required", response.getBody().get("title"));
    }

    @Test
    void shouldHandleDataIntegrityViolation() {
        DataIntegrityViolationException ex =
                new DataIntegrityViolationException("Constraint violation",
                        new RuntimeException("Unique index or primary key violation"));

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                handler.handleDataIntegrity(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertTrue(response.getBody().message().contains("Database integrity violation"));
    }

    @Test
    void shouldHandleGenericException() {
        Exception ex = new Exception("Error inesperado");

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                handler.handleGeneric(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error inesperado", response.getBody().message());
        assertTrue(response.getBody().timestamp().isBefore(LocalDateTime.now().plusSeconds(1)));
    }
}
