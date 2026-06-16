package com.example;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingService {
    final BookingRepository bookingRepository;

    public BookingService (BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public Booking createBooking(Booking booking) {
        return bookingRepository.save(booking);
    }

    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id).orElse(null);
    }

     public List<Booking> getBookingsByGuestId(Long guestId) {
        return bookingRepository.findByGuestId(guestId);
    }
}
