package com.jexige.hotelsapi.controllers.hotel;

import com.jexige.hotelsapi.configs.ApiPaths;
import com.jexige.hotelsapi.model.hotel.Hotel;
import com.jexige.hotelsapi.model.hotel.HotelNotFoundException;
import com.jexige.hotelsapi.model.hotel.HotelService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(ApiPaths.API_PATH + ApiPaths.HOTELS_PATH)
public class HotelController {

    private final HotelService hotelService;
    private final HotelModelAssembler hotelModelAssembler;
    private final PagedResourcesAssembler<Hotel> hotelPagedResourcesAssembler;

    public HotelController(HotelService hotelService, HotelModelAssembler hotelModelAssembler, PagedResourcesAssembler<Hotel> hotelPagedResourcesAssembler) {
        this.hotelService = hotelService;
        this.hotelModelAssembler = hotelModelAssembler;
        this.hotelPagedResourcesAssembler = hotelPagedResourcesAssembler;
    }

    @GetMapping
    public ResponseEntity<?> getAll(Pageable pageable) {
        return ResponseEntity.ok(
                hotelPagedResourcesAssembler.toModel(
                        hotelService.findAll(pageable),
                        hotelModelAssembler
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOne(@PathVariable long id) {
        return hotelService.findById(id)
                .map(hotelModelAssembler::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> postOne(@RequestBody @Valid Hotel newHotel) {
        // The POST method is used only to create new hotels.
        // By setting the id to 0, we make sure that the POST method is NOT used to edit a currently
        // existing hotel.
        newHotel.setId(0);

        final EntityModel<Hotel> hotelEntityModel = hotelModelAssembler.toModel(hotelService.save(newHotel));

        return ResponseEntity
                .created(hotelEntityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(hotelEntityModel);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrSaveOne(@RequestBody @Valid Hotel newHotel, @PathVariable long id) {
        try {
            final EntityModel<Hotel> hotelEntityModel = hotelModelAssembler.toModel(hotelService.update(newHotel, id));
            return ResponseEntity
                    .ok(hotelEntityModel);

        } catch (HotelNotFoundException e) {
            final EntityModel<Hotel> hotelEntityModel = hotelModelAssembler.toModel(hotelService.save(newHotel));
            return ResponseEntity
                    .created(hotelEntityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                    .body(hotelEntityModel);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOne(@PathVariable long id) {
        try {
            hotelService.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (HotelNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
