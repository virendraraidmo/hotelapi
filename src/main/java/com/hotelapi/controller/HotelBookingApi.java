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
