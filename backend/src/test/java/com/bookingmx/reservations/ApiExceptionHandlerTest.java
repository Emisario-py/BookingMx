package com.bookingmx.reservations;

import com.bookingmx.reservations.exception.ApiExceptionHandler;
import com.bookingmx.reservations.exception.BadRequestException;
import com.bookingmx.reservations.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link ApiExceptionHandler}.
 *
 * <p>This class verifies that the exception handler correctly maps custom
 * application exceptions into the expected HTTP status codes. Each test
 * validates one specific exception-handling method.</p>
 */

class ApiExceptionHandlerTest {

    /**
     * Instance of {@link ApiExceptionHandler} used for all tests.
     */
    private final ApiExceptionHandler handler = new ApiExceptionHandler();

    /**
     * Ensures that a {@link BadRequestException} is properly translated into
     * an HTTP 400 Bad Request response.
     */
    @Test
    void badRequest_ShouldReturn400() {
        ResponseEntity<?> res = handler.badRequest(new BadRequestException("Invalid data"));
        assertEquals(400, res.getStatusCodeValue());
    }

    /**
     * Ensures that a {@link NotFoundException} is properly translated into
     * an HTTP 404 Not Found response.
     */
    @Test
    void notFound_ShouldReturn404() {
        ResponseEntity<?> res = handler.notFound(new NotFoundException("Not found"));
        assertEquals(404, res.getStatusCodeValue());
    }

    /**
     * Ensures that a generic, unexpected exception is mapped to an
     * HTTP 500 Internal Server Error response.
     */
    @Test
    void generic_ShouldReturn500() {
        ResponseEntity<?> res = handler.generic(new RuntimeException("Internal"));
        assertEquals(500, res.getStatusCodeValue());
    }
}
