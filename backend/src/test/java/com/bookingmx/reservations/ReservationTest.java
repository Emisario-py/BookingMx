package com.bookingmx.reservations;

import com.bookingmx.reservations.model.Reservation;
import com.bookingmx.reservations.model.ReservationStatus;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link Reservation}, validating constructors,
 * getters/setters, status behavior, equality rules, and hash code consistency.
 *
 * <p>This class ensures that the domain model behaves predictably and
 * consistently, which is especially important when using it inside collections
 * or repository structures.</p>
 */
class ReservationTest {

    /**
     * Validates that the no-args constructor and all setter methods correctly
     * assign values to a {@link Reservation} instance.
     */
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

    /**
     * Validates the parameterized constructor and ensures that newly created
     * reservations default to {@link ReservationStatus#ACTIVE}, also confirming
     * the correctness of the {@link Reservation#isActive()} method.
     */
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

    /**
     * Ensures that {@link Reservation#isActive()} returns {@code false}
     * when the status is explicitly set to {@link ReservationStatus#CANCELED}.
     */
    @Test
    void testIsActiveFalseWhenCanceled() {
        Reservation r = new Reservation();
        r.setStatus(ReservationStatus.CANCELED);
        assertFalse(r.isActive());
    }

    /**
     * Confirms that the equals method returns {@code true} when comparing
     * a reservation instance to itself.
     */
    @Test
    void testEqualsSameObject() {
        Reservation r = new Reservation();
        assertEquals(r, r);
    }

    /**
     * Verifies that equals and hashCode behave consistently when two
     * reservations share the same ID, and differently otherwise.
     */
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

    /**
     * Ensures that comparing a reservation to a different object type returns {@code false}.
     */
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
