package com.jexige.hotelsapi.model.reservation;

public class OverlappingReservationsException extends RuntimeException{
    public OverlappingReservationsException() {
        super("A reservation has already been made in this time period.");
    }
}
