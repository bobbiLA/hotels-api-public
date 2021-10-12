package com.jexige.hotelsapi.controllers.login;

public class RejectedLoginException extends RuntimeException {
    public RejectedLoginException() {
        super("Invalid credentials.");
    }
}
