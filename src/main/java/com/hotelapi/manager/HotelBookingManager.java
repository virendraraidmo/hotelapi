package com.hotelapi.manager;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.hotelapi.dto.Booking;

public class HotelBookingManager implements BookingManager{

    private int numberOfRooms;
    private Map<Integer, List<Booking>> bookingsByRoom;
    private Map<String, List<Booking>> bookingsByGuest;

    public HotelBookingManager(int numberOfRooms) {
        this.numberOfRooms = numberOfRooms;
        this.bookingsByRoom = new ConcurrentHashMap<>();
        this.bookingsByGuest = new ConcurrentHashMap<>();
        for (int i = 1; i <= numberOfRooms; i++) {
            bookingsByRoom.put(i, new ArrayList<>());
        }
    }


    @Override
    public void storeBooking(String guestName, int roomNumber, LocalDate date) throws IllegalArgumentException{

        try {
            // Check if a booking already exists for the given room and date
            for (Booking booking : bookingsByRoom.get(roomNumber)) {
                if (booking.getDate().equals(date)) {
                    throw new IllegalArgumentException("Booking already exists for room " + roomNumber + " on date " + date);
                }
            }

            Booking booking = new Booking(guestName, roomNumber, date);
            bookingsByRoom.get(roomNumber).add(booking);
            bookingsByGuest.computeIfAbsent(guestName, k -> new ArrayList<>()).add(booking);

        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to store booking: " + e.getMessage());
        }
    }

    @Override
    public List<Integer> findAvailableRooms(LocalDate date) throws IllegalArgumentException{
        List<Integer> availableRooms = new ArrayList<>();

        try {
            for (int i = 1; i <= numberOfRooms; i++) {
                boolean isBooked = false;
                for (Booking booking : bookingsByRoom.get(i)) {
                    if (booking.getDate().equals(date)) {
                        isBooked = true;
                        break;
                    }
                }
                if (!isBooked) {
                    availableRooms.add(i);
                }
            }

            return availableRooms;

        } catch (Exception e) {
            throw new IllegalArgumentException("Error finding available rooms: " + e.getMessage());
        }
    }

    @Override
    public List<Booking> findBookingsByGuest(String guestName) throws IllegalArgumentException{
        try {
            return bookingsByGuest.getOrDefault(guestName, new ArrayList<>());
        } catch (Exception e) {
            throw new IllegalArgumentException("Error finding bookings for guest: " + e.getMessage());
        }
    }
}
