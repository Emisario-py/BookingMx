package com.bookingmx.reservations;

import com.bookingmx.reservations.model.Reservation;
import com.bookingmx.reservations.model.ReservationStatus;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ReservationTest {

    @Test
    void testDefaultConstructorAndSetters() {
        Reservation r = new Reservation();
        LocalDate now = LocalDate.now();

        r.setId(1L);
        r.setGuestName("John");
        r.setHotelName("Hotel");
        r.setCheckIn(now.plusDays(2));
        r.setCheckOut(now.plusDays(3));
        r.setStatus(ReservationStatus.CANCELED);

        assertEquals(1L, r.getId());
        assertEquals("John", r.getGuestName());
        assertEquals("Hotel", r.getHotelName());
        assertEquals(now.plusDays(2), r.getCheckIn());
        assertEquals(now.plusDays(3), r.getCheckOut());
        assertEquals(ReservationStatus.CANCELED, r.getStatus());
    }

    @Test
    void testConstructorAndActiveStatus() {
        LocalDate in = LocalDate.now().plusDays(1);
        LocalDate out = LocalDate.now().plusDays(2);

        Reservation r = new Reservation(7L, "A", "B", in, out);

        assertEquals(7L, r.getId());
        assertEquals("A", r.getGuestName());
        assertEquals("B", r.getHotelName());
        assertEquals(in, r.getCheckIn());
        assertEquals(out, r.getCheckOut());
        assertEquals(ReservationStatus.ACTIVE, r.getStatus());
        assertTrue(r.isActive());
    }

    @Test
    void testIsActiveFalseWhenCanceled() {
        Reservation r = new Reservation();
        r.setStatus(ReservationStatus.CANCELED);
        assertFalse(r.isActive());
    }

    @Test
    void testEqualsSameObject() {
        Reservation r = new Reservation();
        assertEquals(r, r);
    }

    @Test
    void testEqualsAndHashCode() {
        Reservation r1 = new Reservation();
        r1.setId(5L);
        Reservation r2 = new Reservation();
        r2.setId(5L);
        Reservation r3 = new Reservation();
        r3.setId(8L);

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
        assertNotEquals(r1, r3);
        assertNotEquals(null, r1);
        assertNotEquals("string", r1);
    }

    @Test
    void equals_ShouldReturnFalse_WhenComparedWithDifferentClass() {

        Reservation reservation = new Reservation(
                1L, "John", "Hotel", LocalDate.now(), LocalDate.now().plusDays(1)
        );

        String otherObject = "I am not a reservation";

        boolean result = reservation.equals(otherObject);

        assertFalse(result, "equals() must return false when comparing with a non-Reservation object");
    }
}
