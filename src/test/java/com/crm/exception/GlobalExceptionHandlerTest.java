package com.crm.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();
    private final WebRequest webRequest = mock(WebRequest.class);

    @Test
    void handleResourceNotFoundException_ShouldReturnNotFound() {

        when(webRequest.getDescription(false)).thenReturn("uri=/api/sellers/1");
        ResourceNotFoundException exception = ResourceNotFoundException.forSeller(1L);


        ResponseEntity<Object> response = exceptionHandler.handleResourceNotFoundException(exception, webRequest);


        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void handleValidationException_ShouldReturnBadRequest() {

        when(webRequest.getDescription(false)).thenReturn("uri=/api/sellers");
        ValidationException exception = ValidationException.forEmptyName();


        ResponseEntity<Object> response = exceptionHandler.handleValidationException(exception, webRequest);


        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void handleDuplicateResourceException_ShouldReturnConflict() {

        when(webRequest.getDescription(false)).thenReturn("uri=/api/sellers");
        DuplicateResourceException exception = DuplicateResourceException.forSeller("Иван");


        ResponseEntity<Object> response = exceptionHandler.handleDuplicateResourceException(exception, webRequest);


        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void handleAnalyticsException_ShouldReturnBadRequest() {

        when(webRequest.getDescription(false)).thenReturn("uri=/api/analytics");
        AnalyticsException exception = AnalyticsException.forNoData();


        ResponseEntity<Object> response = exceptionHandler.handleAnalyticsException(exception, webRequest);


        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void handleGenericException_ShouldReturnInternalServerError() {

        when(webRequest.getDescription(false)).thenReturn("uri=/api/sellers");
        Exception exception = new RuntimeException("Unexpected error");


        ResponseEntity<Object> response = exceptionHandler.handleGenericException(exception, webRequest);


        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}