package com.jexige.hotelsapi.controllers.hotel;

import com.jexige.hotelsapi.configs.ApiPaths;
import com.jexige.hotelsapi.model.hotel.Hotel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class HotelModelAssembler implements RepresentationModelAssembler<Hotel, EntityModel<Hotel>> {

    @Override
    public EntityModel<Hotel> toModel(Hotel hotel) {
        return EntityModel.of(
                hotel,
                linkTo(methodOn(HotelController.class).getOne(hotel.getId())).withSelfRel(),
                linkTo(methodOn(HotelController.class).getAll(null)).withRel(ApiPaths.HOTELS_REL)
        );
    }
}
