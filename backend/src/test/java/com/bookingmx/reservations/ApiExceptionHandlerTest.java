package com.bookingmx.reservations;

import com.bookingmx.reservations.exception.ApiExceptionHandler;
import com.bookingmx.reservations.exception.BadRequestException;
import com.bookingmx.reservations.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class ApiExceptionHandlerTest {

    private final ApiExceptionHandler handler = new ApiExceptionHandler();

    @Test
    void badRequest_ShouldReturn400() {
        ResponseEntity<?> res = handler.badRequest(new BadRequestException("Invalid data"));
        assertEquals(400, res.getStatusCodeValue());
    }

    @Test
    void notFound_ShouldReturn404() {
        ResponseEntity<?> res = handler.notFound(new NotFoundException("Not found"));
        assertEquals(404, res.getStatusCodeValue());
    }

    @Test
    void generic_ShouldReturn500() {
        ResponseEntity<?> res = handler.generic(new RuntimeException("Internal"));
        assertEquals(500, res.getStatusCodeValue());
    }
}
