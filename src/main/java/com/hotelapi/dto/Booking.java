package com.hotelapi.dto;

import java.time.LocalDate;

public class Booking {

    private final String guestName;
    private final int roomNumber;
    private final LocalDate date;

    public Booking(String guestName, int roomNumber, LocalDate date) {
        this.guestName = guestName;
        this.roomNumber = roomNumber;
        this.date = date;
    }

    public String getGuestName() {
        return guestName;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public LocalDate getDate() {
        return date;
    }

}
