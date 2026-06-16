package com.example;

import jakarta.persistence.*;

@Entity
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long guestId;

    private int roomNumber;

    public Long getId() {
        return id;
    }

    public Long getGuestId() {
        return guestId;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

}
