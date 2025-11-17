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

class ReservationServiceTest {

    private ReservationService service;

    @BeforeEach
    void setUp() {
        service = new ReservationService();
    }

    private ReservationRequest buildRequest(int checkInOffset, int checkOutOffset) {
        ReservationRequest req = new ReservationRequest();
        req.setGuestName("Test User");
        req.setHotelName("Test Hotel");
        req.setCheckIn(LocalDate.now().plusDays(checkInOffset));
        req.setCheckOut(LocalDate.now().plusDays(checkOutOffset));
        return req;
    }

    // ────────────────────────────────────────────────────────────────────────────
    // CREATE TESTS
    // ────────────────────────────────────────────────────────────────────────────

    @Test
    void create_ShouldCreateReservation_WhenDatesValid() {
        ReservationRequest req = buildRequest(2, 5);

        Reservation created = service.create(req);

        assertNotNull(created.getId());
        assertEquals("Test User", created.getGuestName());
        assertEquals("Test Hotel", created.getHotelName());
        assertEquals(ReservationStatus.ACTIVE, created.getStatus());
    }


    @Test
    void create_ShouldThrowException_WhenDatesInvalid() {
        ReservationRequest req = buildRequest(5, 3); // check-out BEFORE check-in

        assertThrows(BadRequestException.class, () -> service.create(req));
    }

    // ────────────────────────────────────────────────────────────────────────────
    // UPDATE TESTS
    // ────────────────────────────────────────────────────────────────────────────

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

    @Test
    void update_ShouldThrowNotFound_WhenIdDoesNotExist() {
        ReservationRequest req = buildRequest(2, 3);

        assertThrows(NotFoundException.class, () -> service.update(999L, req));
    }

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

    // ────────────────────────────────────────────────────────────────────────────
    // CANCEL TESTS
    // ────────────────────────────────────────────────────────────────────────────

    @Test
    void cancel_ShouldSetStatusCanceled() {
        Reservation created = service.create(buildRequest(1, 3));

        Reservation canceled = service.cancel(created.getId());

        assertEquals(ReservationStatus.CANCELED, canceled.getStatus());
    }

    @Test
    void cancel_ShouldThrowNotFound_WhenIdDoesNotExist() {
        assertThrows(NotFoundException.class, () -> service.cancel(555L));
    }

    // ────────────────────────────────────────────────────────────────────────────
    // LIST TESTS
    // ────────────────────────────────────────────────────────────────────────────

    @Test
    void list_ShouldReturnAllReservations() {
        service.create(buildRequest(1, 2));
        service.create(buildRequest(2, 3));

        List<Reservation> all = service.list();

        assertEquals(2, all.size());
    }

    // ────────────────────────────────────────────────────────────────────────────
    // VALIDATION TESTS (private method reached through public methods)
    // ────────────────────────────────────────────────────────────────────────────

    @Test
    void create_ShouldThrow_WhenDatesAreNull() {
        ReservationRequest req = new ReservationRequest();
        req.setGuestName("X");
        req.setHotelName("Y");
        req.setCheckIn(null);
        req.setCheckOut(null);

        assertThrows(BadRequestException.class, () -> service.create(req));
    }

    @Test
    void create_ShouldThrow_WhenCheckInIsPast() {
        ReservationRequest req = new ReservationRequest();
        req.setGuestName("A");
        req.setHotelName("B");
        req.setCheckIn(LocalDate.now().minusDays(1));
        req.setCheckOut(LocalDate.now().plusDays(2));

        assertThrows(BadRequestException.class, () -> service.create(req));
    }

    @Test
    void create_ShouldThrow_WhenCheckOutIsPast() {
        ReservationRequest req = new ReservationRequest();
        req.setGuestName("A");
        req.setHotelName("B");
        req.setCheckIn(LocalDate.now().plusDays(2));
        req.setCheckOut(LocalDate.now().minusDays(1));

        assertThrows(BadRequestException.class, () -> service.create(req));
    }
    @Test
    void validateDates_ShouldFail_WhenCheckInIsNull() {
        ReservationRequest req = new ReservationRequest();
        req.setGuestName("A");
        req.setHotelName("B");
        req.setCheckIn(null);
        req.setCheckOut(LocalDate.now().plusDays(2));

        assertThrows(BadRequestException.class, () -> service.create(req));
    }

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
