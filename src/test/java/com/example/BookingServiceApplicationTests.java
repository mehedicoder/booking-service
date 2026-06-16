package com.example;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BookingServiceApplicationTests {

	@Autowired
	private BookingRepository bookingRepository;

	@Test
	void contextLoads() {
	}

	@Test
	void persistsAndFindsBookingsByGuestId() {
		Booking booking = bookingRepository.save(new Booking(42L, 101));

		assertThat(booking.getId()).isNotNull();
		assertThat(bookingRepository.findByGuestId(42L))
				.extracting(Booking::getRoomNumber)
				.containsExactly(101);
	}

}
