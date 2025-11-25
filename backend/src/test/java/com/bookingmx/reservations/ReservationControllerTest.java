package com.bookingmx.reservations;

import com.bookingmx.reservations.controller.ReservationController;
import com.bookingmx.reservations.dto.ReservationRequest;
import com.bookingmx.reservations.model.Reservation;
import com.bookingmx.reservations.model.ReservationStatus;
import com.bookingmx.reservations.service.ReservationService;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link ReservationController}.
 *
 * <p>This test class ensures that the controller correctly delegates work to
 * the {@link ReservationService}, and that returned responses contain the
 * appropriate mapped data. Each endpoint is tested independently to validate
 * proper request handling, data transformation, and service interaction.</p>
 */
class ReservationControllerTest {

    /** Mocked instance of the reservation service. */
    @Mock
    private ReservationService service;

    /** Controller under test, injected with the mocked service. */
    @InjectMocks
    private ReservationController controller;

    /**
     * Initializes Mockito annotations before each test.
     */
    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Verifies that the controller properly maps reservation domain objects into
     * response DTOs when listing reservations.
     */
    @Test
    void list_ShouldReturnMappedResponses() {
        Reservation r = new Reservation(1L, "John", "Beach Resort",
                LocalDate.now().plusDays(1), LocalDate.now().plusDays(2));
        when(service.list()).thenReturn(List.of(r));

        var responses = controller.list();
        assertEquals(1, responses.size());
        assertEquals("John", responses.get(0).getGuestName());
    }

    /**
     * Ensures that creating a reservation delegates to the service and returns
     * the properly mapped response DTO.
     */
    @Test
    void create_ShouldReturnResponse() {
        ReservationRequest req = new ReservationRequest();
        req.setGuestName("Jane");
        req.setHotelName("Palace");
        req.setCheckIn(LocalDate.now().plusDays(2));
        req.setCheckOut(LocalDate.now().plusDays(3));

        Reservation r = new Reservation(1L, "Jane", "Palace",
                req.getCheckIn(), req.getCheckOut());
        when(service.create(req)).thenReturn(r);

        var response = controller.create(req);
        assertEquals("Jane", response.getGuestName());
        verify(service, times(1)).create(req);
    }

    /**
     * Ensures that updating an existing reservation returns the correctly
     * mapped updated reservation.
     */
    @Test
    void update_ShouldReturnResponse() {
        ReservationRequest req = new ReservationRequest();
        req.setGuestName("Mike");
        req.setHotelName("Grand Hotel");
        req.setCheckIn(LocalDate.now().plusDays(3));
        req.setCheckOut(LocalDate.now().plusDays(5));

        Reservation r = new Reservation(1L, "Mike", "Grand Hotel",
                req.getCheckIn(), req.getCheckOut());
        when(service.update(1L, req)).thenReturn(r);

        var response = controller.update(1L, req);
        assertEquals("Mike", response.getGuestName());
    }

    /**
     * Validates that canceling a reservation correctly returns a reservation
     * with a {@link ReservationStatus#CANCELED} status and that the service is
     * invoked as expected.
     */
    @Test
    void cancel_ShouldReturnCanceledReservation() {
        Reservation r = new Reservation(1L, "Nina", "River View",
                LocalDate.now().plusDays(2), LocalDate.now().plusDays(3));
        r.setStatus(ReservationStatus.CANCELED);
        when(service.cancel(1L)).thenReturn(r);

        var response = controller.cancel(1L);
        assertEquals(ReservationStatus.CANCELED, response.getStatus());
        verify(service, times(1)).cancel(1L);
    }
}
