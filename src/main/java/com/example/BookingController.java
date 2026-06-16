package com.example;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/bookings")
public class BookingController {
    final BookingService bookingService;

    public BookingController (BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping("create")
    public Booking createBooking(@RequestBody Booking booking) {
        return ResponseEntity.ok(bookingService.createBooking(booking)).getBody();
    }

    @GetMapping("{guestId}")
    public ResponseEntity<List<Booking>> getBookingsByGuestId(@PathVariable Long guestId) {
        return ResponseEntity.ok(bookingService.getBookingsByGuestId(guestId));
    }
}
