package com.jexige.hotelsapi.controllers.reservation;

import com.jexige.hotelsapi.configs.ApiPaths;
import com.jexige.hotelsapi.model.reservation.OverlappingReservationsException;
import com.jexige.hotelsapi.model.reservation.Reservation;
import com.jexige.hotelsapi.model.reservation.ReservationNotFoundException;
import com.jexige.hotelsapi.model.reservation.ReservationService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.mediatype.problem.Problem;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping(ApiPaths.API_PATH + ApiPaths.RESERVATIONS_PATH)
public class ReservationController {

    private final ReservationService reservationService;
    private final ReservationModelAssembler reservationModelAssembler;
    private final PagedResourcesAssembler<Reservation> reservationPagedResourcesAssembler;

    public ReservationController(ReservationService reservationService, ReservationModelAssembler reservationModelAssembler, PagedResourcesAssembler<Reservation> reservationPagedResourcesAssembler) {
        this.reservationService = reservationService;
        this.reservationModelAssembler = reservationModelAssembler;
        this.reservationPagedResourcesAssembler = reservationPagedResourcesAssembler;
    }

    @GetMapping
    public ResponseEntity<?> getAll(Pageable pageable) {
        return ResponseEntity.ok(
                reservationPagedResourcesAssembler.toModel(
                        reservationService.findAll(pageable),
                        reservationModelAssembler
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOne(@PathVariable long id) {
        return reservationService.findById(id)
                .map(reservationModelAssembler::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> postOne(@RequestBody @Valid Reservation newReservation) {
        // The POST method is used only to create new reservations.
        // By setting the id to 0, we make sure that the POST method is NOT used to edit a currently
        // existing reservation.
        newReservation.setId(0);

        final EntityModel<Reservation> reservationEntityModel = reservationModelAssembler.toModel(reservationService.save(newReservation));

        return ResponseEntity
                .created(reservationEntityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(reservationEntityModel);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrSaveOne(@RequestBody @Valid Reservation newReservation, @PathVariable long id) {
        try {
            final EntityModel<Reservation> reservationEntityModel = reservationModelAssembler.toModel(reservationService.update(newReservation, id));
            return ResponseEntity
                    .ok(reservationEntityModel);

        } catch (ReservationNotFoundException e) {
            final EntityModel<Reservation> reservationEntityModel = reservationModelAssembler.toModel(reservationService.save(newReservation));
            return ResponseEntity
                    .created(reservationEntityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                    .body(reservationEntityModel);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOne(@PathVariable long id) {
        try {
            reservationService.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (ReservationNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @ExceptionHandler(OverlappingReservationsException.class)
    public ResponseEntity<?> handleOverlappingReservationsException(OverlappingReservationsException exception, HttpServletRequest request) {
        final HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;

        return ResponseEntity
                .status(status)
                .header(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE)
                .body(Problem.create()
                        .withStatus(status)
                        .withInstance(URI.create(request.getServletPath()))
                        .withTitle(status.getReasonPhrase())
                        .withDetail(exception.getMessage())
                );
    }
}
