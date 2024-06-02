package com.hotelapi.manager;

import java.time.LocalDate;
import java.util.List;

import com.hotelapi.dto.Booking;

public interface BookingManager {

    void storeBooking(String guestName, int roomNumber, LocalDate date) throws Exception;
    List<Integer> findAvailableRooms(LocalDate date) throws Exception;
    List<Booking> findBookingsByGuest(String guestName) throws Exception;

}
