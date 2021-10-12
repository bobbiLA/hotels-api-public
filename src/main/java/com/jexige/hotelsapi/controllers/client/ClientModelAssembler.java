package com.jexige.hotelsapi.controllers.client;

import com.jexige.hotelsapi.configs.ApiPaths;
import com.jexige.hotelsapi.model.client.Client;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ClientModelAssembler implements RepresentationModelAssembler<Client, EntityModel<Client>> {

    @Override
    public EntityModel<Client> toModel(Client client) {
        return EntityModel.of(
                client,
                linkTo(methodOn(ClientController.class).getOne(client.getId())).withSelfRel(),
                linkTo(methodOn(ClientController.class).getAll(null)).withRel(ApiPaths.CLIENTS_REL)
        );
    }
}
