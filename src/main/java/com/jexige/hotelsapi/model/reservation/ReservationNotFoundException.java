package com.jexige.hotelsapi.model.reservation;

public class ReservationNotFoundException extends RuntimeException {
    public ReservationNotFoundException(long id) {
        super("Reservation with id " + id + " does not exist.");
    }
}
