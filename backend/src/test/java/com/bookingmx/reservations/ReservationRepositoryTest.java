package com.bookingmx.reservations;

import com.bookingmx.reservations.model.Reservation;
import com.bookingmx.reservations.model.ReservationStatus;
import com.bookingmx.reservations.repo.ReservationRepository;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link ReservationRepository}.
 *
 * <p>This class validates the behavior of the in-memory repository used for storing
 * and retrieving {@link Reservation} objects. It ensures that all CRUD operations
 * behave as expected, including ID assignment, updates, lookups, and deletions.</p>
 */
class ReservationRepositoryTest {

    /** Repository instance used for all test cases. */
    private ReservationRepository repo;

    /**
     * Initializes a fresh repository before each test to ensure isolated test cases.
     */
    @BeforeEach
    void setUp() {
        repo = new ReservationRepository();
    }

    /**
     * Utility method to create a new reservation with default values.
     *
     * @param name the guest name to assign to the reservation
     * @return a new {@link Reservation} instance with future check-in/out dates
     */
    private Reservation newReservation(String name) {
        Reservation r = new Reservation();
        r.setGuestName(name);
        r.setHotelName("Test Hotel");
        r.setCheckIn(LocalDate.now().plusDays(1));
        r.setCheckOut(LocalDate.now().plusDays(2));
        r.setStatus(ReservationStatus.ACTIVE);
        return r;
    }

    /**
     * Ensures that saving a new reservation automatically assigns an ID.
     */
    @Test
    void save_ShouldAssignId_WhenNewReservation() {
        Reservation r = newReservation("Alice");
        Reservation saved = repo.save(r);

        assertNotNull(saved.getId(), "The repository should assign an ID");
        assertEquals("Alice", saved.getGuestName());
    }

    /**
     * Validates that saving a reservation with an existing ID updates the stored entry.
     */
    @Test
    void save_ShouldUpdate_WhenIdAlreadyExists() {
        Reservation r = newReservation("Bob");
        Reservation saved = repo.save(r);

        saved.setHotelName("Updated Hotel");
        Reservation updated = repo.save(saved);

        assertEquals("Updated Hotel", repo.findById(saved.getId()).get().getHotelName());
        assertEquals(saved.getId(), updated.getId(), "The same ID should be reused");
    }

    /**
     * Ensures that {@link ReservationRepository#findById(Long)} returns an
     * existing reservation when it is present.
     */
    @Test
    void findById_ShouldReturnReservation_WhenExists() {
        Reservation r = newReservation("Charlie");
        Reservation saved = repo.save(r);

        Optional<Reservation> found = repo.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("Charlie", found.get().getGuestName());
    }

    /**
     * Ensures that requesting a non-existent ID returns an empty {@link Optional}.
     */
    @Test
    void findById_ShouldReturnEmpty_WhenNotFound() {
        Optional<Reservation> result = repo.findById(999L);
        assertTrue(result.isEmpty(), "Non-existent ID should return Optional.empty()");
    }

    /**
     * Validates that all saved reservations are returned when calling {@link ReservationRepository#findAll()}.
     */
    @Test
    void findAll_ShouldReturnAllSavedReservations() {
        repo.save(newReservation("David"));
        repo.save(newReservation("Eve"));

        List<Reservation> all = repo.findAll();
        assertEquals(2, all.size());
        assertTrue(all.stream().anyMatch(r -> r.getGuestName().equals("David")));
        assertTrue(all.stream().anyMatch(r -> r.getGuestName().equals("Eve")));
    }

    /**
     * Ensures that deleting a reservation by ID removes it from the repository.
     */
    @Test
    void delete_ShouldRemoveReservation() {
        Reservation r = repo.save(newReservation("Frank"));
        Long id = r.getId();

        repo.delete(id);
        assertTrue(repo.findById(id).isEmpty(), "Deleted reservation should not be found");
    }

    /**
     * Ensures that attempting to delete a non-existent ID does not result in an exception.
     */
    @Test
    void delete_ShouldNotFail_WhenIdDoesNotExist() {
        assertDoesNotThrow(() -> repo.delete(123L), "Deleting a non-existent ID should not throw");
    }
}
