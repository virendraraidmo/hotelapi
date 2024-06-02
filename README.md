
# Hotel Booking API:

This is a simple Java-based microservice API for managing hotel bookings. It provides endpoints for storing bookings, finding available rooms on a given date, and retrieving bookings for a specific guest.

#Features : 

1. Store Booking: Endpoint to store a booking with guest name, room number, and date.
2. Find Available Rooms: Endpoint to find available rooms for a given date.
3. Find Bookings for Guest: Endpoint to find all bookings for a specific guest.

# Prerequisites :

1. Java Development Kit (JDK) installed on your machine
2. Maven or Gradle for building the project
3. Git for cloning the repository (optional)

# Installation:

1. Clone the repository to your local machine:

2. Navigate to the project directory:

3. Build the project using Maven

#




#Usage

1. Run the main class HotelBookingApi to start the HTTP server:
java -cp target/classes com.devtest.controller.HotelBookingApi

2. Use a tool like cURL or Postman to make HTTP requests to the API endpoints:

-Store Booking:
POST /bookings

URL : http://localhost:8080/bookings

-Find Available Rooms:
GET /available-rooms?date={date}

URL : http://localhost:8080/available-rooms?date=2024-06-02

-Find Bookings for Guest:
GET /bookings-for-guest?guestName={guestName}

URL : http://localhost:8080/bookings-for-guest?guestName=Virendra%20Joe 



## Process to create the Hotel Booking Application


for reating a Simple Hotel Booking application in Java can be done using a simple implementation using Java, HTML, CSS, and JavaScript.

Here's a basic overview of what we'll do:

Frontend: Create a basic HTML/CSS/JS interface for the Hotel Booking api .
Backend: Develop a Java application that handles the chatbot logic.
Database: Utilize Supabase as the database for storing Hotel Booking information.


## Backend (Java):
#Main Classes 

# Booking.java

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

------------------------------------------------
# HotelBookingApi.java

package com.hotelapi.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.hotelapi.dto.Booking;
import com.hotelapi.manager.BookingManager;
import com.hotelapi.manager.HotelBookingManager;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import javax.servlet.http.HttpServletResponse;



public class HotelBookingApi {

    private static final Logger LOGGER = Logger.getLogger(HotelBookingApi.class.getName());

    private static BookingManager hotelBookingManager = null;

    public static void main(String[] args) throws IOException {

        hotelBookingManager = new HotelBookingManager(10);

        // Create HTTP server
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // Define HTTP endpoints
        server.createContext("/bookings", new BookingsHandler());
        server.createContext("/available-rooms", new AvailableRoomsHandler());
        server.createContext("/bookings-for-guest", new BookingsForGuestHandler());

        // Set logger level
        LOGGER.setLevel(Level.INFO);

        // Start the server
        server.start();
        System.out.println("Server started on port 8080");
    }

    static class BookingsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            LOGGER.info("Handling booking request...");

            try {
                if ("POST".equals(exchange.getRequestMethod())) {
                    String requestBody = getRequestBody(exchange.getRequestBody());
                    Map<String, String> params = Utils.parseQuery(requestBody);

                    String guestName = params.get("guestName");
                    int roomNumber = Integer.parseInt(params.get("roomNumber"));
                    LocalDate date = LocalDate.parse(params.get("date"));

                    // Store the booking
                    hotelBookingManager.storeBooking(guestName, roomNumber, date);

                    String response = "{\"message\": \"Booking stored successfully\"}";
                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    exchange.sendResponseHeaders(HttpServletResponse.SC_OK, response.length());
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }
                } else {
                    LOGGER.log(Level.SEVERE, "Error processing booking request", "Http Method not allow");

                    String response = "{\"error\": \"Method not allow\"}";
                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    exchange.sendResponseHeaders(HttpServletResponse.SC_OK, response.length());
                    exchange.sendResponseHeaders(HttpServletResponse.SC_METHOD_NOT_ALLOWED, response.length());
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }

                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error processing booking request", e);

                String errorMessage = "{\"error\": "+e.getMessage()+"}";
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, errorMessage.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(errorMessage.getBytes());
                }
            }
        }
    }

    static class AvailableRoomsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            LOGGER.info("Handling available room request...");
            try {
                if ("GET".equals(exchange.getRequestMethod())) {
                    // Parse query parameter to get the date
                    String query = exchange.getRequestURI().getQuery();
                    Map<String, String> params = Utils.parseQuery(query);
                    LocalDate date = LocalDate.parse(params.get("date"));

                    // Find available rooms for the given date
                    List<Integer> availableRooms = hotelBookingManager.findAvailableRooms(date);

                    // Send response with available rooms
                    String response = availableRooms.stream().map(Object::toString).collect(Collectors.joining(","));
                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    exchange.sendResponseHeaders(HttpServletResponse.SC_OK, response.length());
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }
                } else {
                    LOGGER.log(Level.SEVERE, "Error processing available room request", "Http Method not allow");

                    String response = "{\"error\": \"Method not allow\"}";
                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    exchange.sendResponseHeaders(HttpServletResponse.SC_OK, response.length());
                    exchange.sendResponseHeaders(HttpServletResponse.SC_METHOD_NOT_ALLOWED, response.length());
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }

                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error processing available room request", e);

                String errorMessage = "{\"error\": "+e.getMessage()+"}";
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, errorMessage.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(errorMessage.getBytes());
                }
            }
        }
    }

    static class BookingsForGuestHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            LOGGER.info("Handling booking-for-guest request...");
            try {
                if ("GET".equals(exchange.getRequestMethod())) {
                    // Parse query parameter to get the guest name
                    String query = exchange.getRequestURI().getQuery();
                    Map<String, String> params = Utils.parseQuery(query);
                    String guestName = params.get("guestName");

                    // Find bookings for the given guest
                    List<Booking> bookingsForGuest = hotelBookingManager.findBookingsByGuest(guestName);

                    // Send response with bookings for the guest
                    String response = bookingsForGuest.toString();
                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    exchange.sendResponseHeaders(HttpServletResponse.SC_OK, response.length());
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }
                } else {
                    LOGGER.log(Level.SEVERE, "Error processing booking-for-guest request", "Http Method not allow");

                    String response = "{\"error\": \"Method not allow\"}";
                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    exchange.sendResponseHeaders(HttpServletResponse.SC_OK, response.length());
                    exchange.sendResponseHeaders(HttpServletResponse.SC_METHOD_NOT_ALLOWED, response.length());
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }

                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error processing booking-for-guest request", e);

                String errorMessage = "{\"error\": "+e.getMessage()+"}";
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, errorMessage.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(errorMessage.getBytes());
                }
            }
        }
    }

    static class Utils {
        public static Map<String, String> parseQuery(String query) {
            Map<String, String> queryParams = new HashMap<>();
            if (query != null && !query.isEmpty()) {
                String[] pairs = query.split("&");
                for (String pair : pairs) {
                    String[] keyValue = pair.split("=");
                    if (keyValue.length == 2) {
                        String key = keyValue[0];
                        String value = keyValue[1];
                        queryParams.put(key, value);
                    }
                }
            }
            return queryParams;
        }
    }


    private static String getRequestBody(InputStream inputStream) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            return br.lines().collect(Collectors.joining(System.lineSeparator()));
        }
    }
}
------------------------------------------------------------
#BookingManager.java

package com.hotelapi.manager;

import java.time.LocalDate;
import java.util.List;

import com.hotelapi.dto.Booking;

public interface BookingManager {

    void storeBooking(String guestName, int roomNumber, LocalDate date) throws Exception;
    List<Integer> findAvailableRooms(LocalDate date) throws Exception;
    List<Booking> findBookingsByGuest(String guestName) throws Exception;

}

---------------------------------------------------------------------
# HotelBookingManager.java

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
--------------------------------------------------------------------------
# Build Configuration (Maven)

# pom.xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.devtest</groupId>
    <artifactId>hotel-api</artifactId>
    <version>1.0-SNAPSHOT</version>
    <name>Hotel Booking Manager</name>
    <description>Simple hotel booking manager micro-service</description>

    <dependencies>

        <!-- javax -->
        <dependency>
            <groupId>javax.servlet</groupId>
            <artifactId>javax.servlet-api</artifactId>
            <version>4.0.1</version> <!-- Or the version you need -->
            <scope>provided</scope>
        </dependency>


        <!-- JUnit for testing -->
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.8.1</version>
            <scope>test</scope>
        </dependency>



    </dependencies>

    <build>
        <plugins>
            <!-- Maven Compiler Plugin -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.8.1</version>
                <configuration>
                    <source>1.8</source>
                    <target>1.8</target>
                </configuration>
            </plugin>

            <!-- Maven JAR Plugin -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-jar-plugin</artifactId>
                <version>3.2.0</version>
                <configuration>
                    <archive>
                        <manifest>
                            <mainClass>com.yourcompany.HttpServer</mainClass> <!-- Specify your main class here -->
                        </manifest>
                    </archive>
                </configuration>
            </plugin>
        </plugins>
    </build>

</project>


# HotelBookingManagerTest.java 

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







