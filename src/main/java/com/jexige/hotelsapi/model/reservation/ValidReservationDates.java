package com.jexige.hotelsapi.model.reservation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


@Target({TYPE, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = {ValidReservationDatesValidator.class})
@Documented
public @interface ValidReservationDates {
    String message() default "endDate cannot precede startDate.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
