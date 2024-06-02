import com.hotelapi.dto.Booking;
import com.hotelapi.manager.HotelBookingManager;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import static org.junit.Assert.*;

public class HotelBookingManagerTest {

    private HotelBookingManager bookingManager;

    @Before
    public void setUp() {
        bookingManager = new HotelBookingManager(3); // Create a booking manager with 3 rooms
    }

    @Test
    public void testStoreBooking() {
        bookingManager.storeBooking("guest_1", 1, LocalDate.now());
        assertEquals(1, bookingManager.findBookingsByGuest("guest_1").size());
    }

    @Test
    public void testFindAvailableRooms() {
        // Assuming we have booked room 1 for today
        bookingManager.storeBooking("guest_1", 1, LocalDate.now());

        // Finding available rooms for tomorrow should return all rooms
        List<Integer> availableRooms = bookingManager.findAvailableRooms(LocalDate.now().plusDays(1));
        assertEquals(3, availableRooms.size());
        assertTrue(availableRooms.contains(1));
        assertTrue(availableRooms.contains(2));
        assertTrue(availableRooms.contains(3));

        // Booking room 1 for tomorrow
        bookingManager.storeBooking("guest_2", 1, LocalDate.now().plusDays(1));
        // Booking room 2 for tomorrow
        bookingManager.storeBooking("guest_3", 2, LocalDate.now().plusDays(1));
        // Now, only room 3 should be available for tomorrow
        availableRooms = bookingManager.findAvailableRooms(LocalDate.now().plusDays(1));
        assertEquals(1, availableRooms.size());
        assertTrue(availableRooms.contains(3));
    }

    @Test
    public void testFindBookingsByGuest() {
        bookingManager.storeBooking("guest_1", 1, LocalDate.now());
        bookingManager.storeBooking("guest_1", 2, LocalDate.now());
        bookingManager.storeBooking("guest_2", 3, LocalDate.now());

        List<Booking> guest_1sBookings = bookingManager.findBookingsByGuest("guest_1");
        assertEquals(2, guest_1sBookings.size());
        assertEquals("guest_1", guest_1sBookings.get(0).getGuestName());
        assertEquals(1, guest_1sBookings.get(0).getRoomNumber());
        assertEquals("guest_1", guest_1sBookings.get(1).getGuestName());
        assertEquals(2, guest_1sBookings.get(1).getRoomNumber());

        List<Booking> guest_2Bookings = bookingManager.findBookingsByGuest("guest_2");
        assertEquals(1, guest_2Bookings.size());
        assertEquals("guest_2", guest_2Bookings.get(0).getGuestName());
        assertEquals(3, guest_2Bookings.get(0).getRoomNumber());
    }

}
