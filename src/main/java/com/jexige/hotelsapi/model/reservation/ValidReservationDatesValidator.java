package com.jexige.hotelsapi.model.reservation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidReservationDatesValidator implements ConstraintValidator<ValidReservationDates, Reservation> {

    @Override
    public void initialize(ValidReservationDates constraintAnnotation) {
    }

    @Override
    public boolean isValid(Reservation reservation, ConstraintValidatorContext context) {
        if (reservation.getStartDate() != null && reservation.getEndDate() != null) {
            return reservation.getStartDate().isBefore(reservation.getEndDate());
        }
        return true;
    }
}
