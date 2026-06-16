package com.example;

import jakarta.persistence.*;

@Entity
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long guestId;

    private int roomNumber;

    public Booking() {
    }

    public Booking(Long guestId, int roomNumber) {
        this.guestId = guestId;
        this.roomNumber = roomNumber;
    }

    public Long getId() {
        return id;
    }

    public Long getGuestId() {
        return guestId;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public void setGuestId(Long guestId) {
        this.guestId = guestId;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

}
