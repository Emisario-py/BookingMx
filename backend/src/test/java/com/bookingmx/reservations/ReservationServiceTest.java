package com.bookingmx.reservations;

import com.bookingmx.reservations.dto.ReservationRequest;
import com.bookingmx.reservations.exception.BadRequestException;
import com.bookingmx.reservations.exception.NotFoundException;
import com.bookingmx.reservations.model.Reservation;
import com.bookingmx.reservations.model.ReservationStatus;
import com.bookingmx.reservations.service.ReservationService;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link ReservationService}.
 *
 * <p>This test suite validates all reservation-related business logic,
 * including reservation creation, modification, cancellation, and date validation.
 * It ensures that the service enforces all application rules and throws
 * appropriate exceptions when necessary.</p>
 */
class ReservationServiceTest {

    /** Service instance under test. */
    private ReservationService service;

    /**
     * Creates a fresh instance of the service before each test to ensure isolation.
     */
    @BeforeEach
    void setUp() {
        service = new ReservationService();
    }

    /**
     * Utility method for building a request object with dynamic date offsets.
     *
     * @param checkInOffset  number of days from now for check-in
     * @param checkOutOffset number of days from now for check-out
     * @return a pre-filled {@link ReservationRequest}
     */
    private ReservationRequest buildRequest(int checkInOffset, int checkOutOffset) {
        ReservationRequest req = new ReservationRequest();
        req.setGuestName("Test User");
        req.setHotelName("Test Hotel");
        req.setCheckIn(LocalDate.now().plusDays(checkInOffset));
        req.setCheckOut(LocalDate.now().plusDays(checkOutOffset));
        return req;
    }

    /**
     * Ensures that creating a reservation with valid dates succeeds and returns
     * a populated {@link Reservation} object.
     */
    @Test
    void create_ShouldCreateReservation_WhenDatesValid() {
        ReservationRequest req = buildRequest(2, 5);

        Reservation created = service.create(req);

        assertNotNull(created.getId());
        assertEquals("Test User", created.getGuestName());
        assertEquals("Test Hotel", created.getHotelName());
        assertEquals(ReservationStatus.ACTIVE, created.getStatus());
    }

    /**
     * Ensures that creating a reservation with invalid date order throws a
     * {@link BadRequestException}.
     */
    @Test
    void create_ShouldThrowException_WhenDatesInvalid() {
        ReservationRequest req = buildRequest(5, 3); // check-out BEFORE check-in

        assertThrows(BadRequestException.class, () -> service.create(req));
    }

    /**
     * Ensures that updating an existing reservation applies new values correctly.
     */
    @Test
    void update_ShouldModifyExistingReservation() {
        // First create
        Reservation created = service.create(buildRequest(2, 4));

        // Now update
        ReservationRequest newData = new ReservationRequest();
        newData.setGuestName("New Name");
        newData.setHotelName("New Hotel");
        newData.setCheckIn(LocalDate.now().plusDays(3));
        newData.setCheckOut(LocalDate.now().plusDays(6));

        Reservation updated = service.update(created.getId(), newData);

        assertEquals("New Name", updated.getGuestName());
        assertEquals("New Hotel", updated.getHotelName());
        assertEquals(newData.getCheckIn(), updated.getCheckIn());
        assertEquals(newData.getCheckOut(), updated.getCheckOut());
    }

    /**
     * Ensures that trying to update a non-existent reservation ID results in
     * a {@link NotFoundException}.
     */
    @Test
    void update_ShouldThrowNotFound_WhenIdDoesNotExist() {
        ReservationRequest req = buildRequest(2, 3);

        assertThrows(NotFoundException.class, () -> service.update(999L, req));
    }

    /**
     * Ensures that updating a canceled reservation is forbidden. A
     * {@link BadRequestException} should be thrown.
     */
    @Test
    void update_ShouldThrowBadRequest_WhenReservationIsCanceled() {
        Reservation created = service.create(buildRequest(2, 3));

        // Cancel first
        service.cancel(created.getId());

        ReservationRequest req = buildRequest(3, 4);

        assertThrows(BadRequestException.class, () ->
                service.update(created.getId(), req)
        );
    }

    /**
     * Ensures that canceling a reservation updates its status to
     * {@link ReservationStatus#CANCELED}.
     */
    @Test
    void cancel_ShouldSetStatusCanceled() {
        Reservation created = service.create(buildRequest(1, 3));

        Reservation canceled = service.cancel(created.getId());

        assertEquals(ReservationStatus.CANCELED, canceled.getStatus());
    }

    /**
     * Ensures that canceling a non-existent reservation results in a
     * {@link NotFoundException}.
     */
    @Test
    void cancel_ShouldThrowNotFound_WhenIdDoesNotExist() {
        assertThrows(NotFoundException.class, () -> service.cancel(555L));
    }

    /**
     * Ensures that all created reservations are returned when calling {@link ReservationService#list()}.
     */
    @Test
    void list_ShouldReturnAllReservations() {
        service.create(buildRequest(1, 2));
        service.create(buildRequest(2, 3));

        List<Reservation> all = service.list();

        assertEquals(2, all.size());
    }

    /**
     * Ensures that missing check-in and check-out dates result in a
     * {@link BadRequestException}.
     */
    @Test
    void create_ShouldThrow_WhenDatesAreNull() {
        ReservationRequest req = new ReservationRequest();
        req.setGuestName("X");
        req.setHotelName("Y");
        req.setCheckIn(null);
        req.setCheckOut(null);

        assertThrows(BadRequestException.class, () -> service.create(req));
    }

    /**
     * Ensures validation fails when check-in is set to a past date.
     */
    @Test
    void create_ShouldThrow_WhenCheckInIsPast() {
        ReservationRequest req = new ReservationRequest();
        req.setGuestName("A");
        req.setHotelName("B");
        req.setCheckIn(LocalDate.now().minusDays(1));
        req.setCheckOut(LocalDate.now().plusDays(2));

        assertThrows(BadRequestException.class, () -> service.create(req));
    }

    /**
     * Ensures validation fails when check-out occurs before today.
     */
    @Test
    void create_ShouldThrow_WhenCheckOutIsPast() {
        ReservationRequest req = new ReservationRequest();
        req.setGuestName("A");
        req.setHotelName("B");
        req.setCheckIn(LocalDate.now().plusDays(2));
        req.setCheckOut(LocalDate.now().minusDays(1));

        assertThrows(BadRequestException.class, () -> service.create(req));
    }

    /**
     * Ensures validation fails when check-in is null.
     */
    @Test
    void validateDates_ShouldFail_WhenCheckInIsNull() {
        ReservationRequest req = new ReservationRequest();
        req.setGuestName("A");
        req.setHotelName("B");
        req.setCheckIn(null);
        req.setCheckOut(LocalDate.now().plusDays(2));

        assertThrows(BadRequestException.class, () -> service.create(req));
    }

    /**
     * Ensures validation fails when check-out is null.
     */
    @Test
    void validateDates_ShouldFail_WhenCheckOutIsNull() {
        ReservationRequest req = new ReservationRequest();
        req.setGuestName("A");
        req.setHotelName("B");
        req.setCheckIn(LocalDate.now().plusDays(2));
        req.setCheckOut(null);

        assertThrows(BadRequestException.class, () -> service.create(req));
    }

}
