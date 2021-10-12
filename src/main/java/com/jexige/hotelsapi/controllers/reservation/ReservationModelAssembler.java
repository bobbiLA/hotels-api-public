package com.jexige.hotelsapi.controllers.reservation;

import com.jexige.hotelsapi.configs.ApiPaths;
import com.jexige.hotelsapi.model.reservation.Reservation;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ReservationModelAssembler implements RepresentationModelAssembler<Reservation, EntityModel<Reservation>> {

    @Override
    public EntityModel<Reservation> toModel(Reservation reservation) {
        return EntityModel.of(
                reservation,
                linkTo(methodOn(ReservationController.class).getOne(reservation.getId())).withSelfRel(),
                linkTo(methodOn(ReservationController.class).getAll(null)).withRel(ApiPaths.RESERVATIONS_REL)
        );
    }
}
