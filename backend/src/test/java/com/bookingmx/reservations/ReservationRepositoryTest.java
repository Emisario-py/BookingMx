package com.bookingmx.reservations;

import com.bookingmx.reservations.model.Reservation;
import com.bookingmx.reservations.model.ReservationStatus;
import com.bookingmx.reservations.repo.ReservationRepository;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ReservationRepositoryTest {

    private ReservationRepository repo;

    @BeforeEach
    void setUp() {
        repo = new ReservationRepository();
    }

    private Reservation newReservation(String name) {
        Reservation r = new Reservation();
        r.setGuestName(name);
        r.setHotelName("Test Hotel");
        r.setCheckIn(LocalDate.now().plusDays(1));
        r.setCheckOut(LocalDate.now().plusDays(2));
        r.setStatus(ReservationStatus.ACTIVE);
        return r;
    }

    @Test
    void save_ShouldAssignId_WhenNewReservation() {
        Reservation r = newReservation("Alice");
        Reservation saved = repo.save(r);

        assertNotNull(saved.getId(), "The repository should assign an ID");
        assertEquals("Alice", saved.getGuestName());
    }

    @Test
    void save_ShouldUpdate_WhenIdAlreadyExists() {
        Reservation r = newReservation("Bob");
        Reservation saved = repo.save(r);

        saved.setHotelName("Updated Hotel");
        Reservation updated = repo.save(saved);

        assertEquals("Updated Hotel", repo.findById(saved.getId()).get().getHotelName());
        assertEquals(saved.getId(), updated.getId(), "The same ID should be reused");
    }

    @Test
    void findById_ShouldReturnReservation_WhenExists() {
        Reservation r = newReservation("Charlie");
        Reservation saved = repo.save(r);

        Optional<Reservation> found = repo.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("Charlie", found.get().getGuestName());
    }

    @Test
    void findById_ShouldReturnEmpty_WhenNotFound() {
        Optional<Reservation> result = repo.findById(999L);
        assertTrue(result.isEmpty(), "Non-existent ID should return Optional.empty()");
    }

    @Test
    void findAll_ShouldReturnAllSavedReservations() {
        repo.save(newReservation("David"));
        repo.save(newReservation("Eve"));

        List<Reservation> all = repo.findAll();
        assertEquals(2, all.size());
        assertTrue(all.stream().anyMatch(r -> r.getGuestName().equals("David")));
        assertTrue(all.stream().anyMatch(r -> r.getGuestName().equals("Eve")));
    }

    @Test
    void delete_ShouldRemoveReservation() {
        Reservation r = repo.save(newReservation("Frank"));
        Long id = r.getId();

        repo.delete(id);
        assertTrue(repo.findById(id).isEmpty(), "Deleted reservation should not be found");
    }

    @Test
    void delete_ShouldNotFail_WhenIdDoesNotExist() {
        assertDoesNotThrow(() -> repo.delete(123L), "Deleting a non-existent ID should not throw");
    }
}
