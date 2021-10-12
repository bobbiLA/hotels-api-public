package com.jexige.hotelsapi.controllers.client;

import com.jexige.hotelsapi.configs.ApiPaths;
import com.jexige.hotelsapi.model.client.Client;
import com.jexige.hotelsapi.model.client.ClientNotFoundException;
import com.jexige.hotelsapi.model.client.ClientService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(ApiPaths.API_PATH + ApiPaths.CLIENTS_PATH)
public class ClientController {

    private final ClientService clientService;
    private final ClientModelAssembler clientModelAssembler;
    private final PagedResourcesAssembler<Client> clientPagedResourcesAssembler;

    public ClientController(ClientService clientService, ClientModelAssembler clientModelAssembler, PagedResourcesAssembler<Client> clientPagedResourcesAssembler) {
        this.clientService = clientService;
        this.clientModelAssembler = clientModelAssembler;
        this.clientPagedResourcesAssembler = clientPagedResourcesAssembler;
    }

    @GetMapping
    public ResponseEntity<?> getAll(Pageable pageable) {
        return ResponseEntity.ok(
                clientPagedResourcesAssembler.toModel(
                        clientService.findAll(pageable),
                        clientModelAssembler
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOne(@PathVariable long id) {
        return clientService.findById(id)
                .map(clientModelAssembler::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> postOne(@RequestBody @Valid Client newClient) {
        // The POST method is used only to create new clients.
        // By setting the id to 0, we make sure that the POST method is NOT used to edit a currently
        // existing client.
        newClient.setId(0);

        final EntityModel<Client> clientEntityModel = clientModelAssembler.toModel(clientService.save(newClient));

        return ResponseEntity
                .created(clientEntityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(clientEntityModel);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrSaveOne(@RequestBody @Valid Client newClient, @PathVariable long id) {
        try {
            final EntityModel<Client> clientEntityModel = clientModelAssembler.toModel(clientService.update(newClient, id));
            return ResponseEntity
                    .ok(clientEntityModel);

        } catch (ClientNotFoundException e) {
            final EntityModel<Client> clientEntityModel = clientModelAssembler.toModel(clientService.save(newClient));
            return ResponseEntity
                    .created(clientEntityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                    .body(clientEntityModel);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOne(@PathVariable long id) {
        try {
            clientService.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (ClientNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
