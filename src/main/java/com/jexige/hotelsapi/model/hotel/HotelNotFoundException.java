package com.jexige.hotelsapi.model.hotel;

public class HotelNotFoundException extends RuntimeException {
    public HotelNotFoundException(long id) {
        super("Hotel with id " + id + " does not exist.");
    }
}
