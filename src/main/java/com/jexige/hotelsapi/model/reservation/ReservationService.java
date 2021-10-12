package com.jexige.hotelsapi.model.reservation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;

    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public Optional<Reservation> findById(long id) {
        return reservationRepository.findById(id);
    }

    public Page<Reservation> findAll(Pageable pageable) {
        return reservationRepository.findAll(pageable);
    }

    public Reservation save(Reservation newReservation) throws OverlappingReservationsException {
        if (hasOverlappingReservations(newReservation)) {
            throw new OverlappingReservationsException();
        }
        return reservationRepository.save(newReservation);
    }

    public boolean hasOverlappingReservations(Reservation reservation) {
        return !reservationRepository.findOverlappingReservations(
                reservation.getId(),
//                reservation.getClient().getId(), // Note : A same room cannot be reserved twice in the same time period, whoever the client is
                reservation.getHotel().getId(),
                reservation.getStartDate(),
                reservation.getEndDate(),
                reservation.getRoomId()
        ).isEmpty();
    }

    public Reservation update(Reservation newReservation, long id) throws ReservationNotFoundException, OverlappingReservationsException {
        final Optional<Reservation> optionalReservation = reservationRepository.findById(id);
        if (optionalReservation.isEmpty()) {
            throw new ReservationNotFoundException(id);
        } else {
            return save(newReservation);
        }
    }

    public void deleteById(long id) throws ReservationNotFoundException {
        final Optional<Reservation> opt = findById(id);
        if (opt.isEmpty()) {
            throw new ReservationNotFoundException(id);
        } else {
            reservationRepository.deleteById(id);
        }
    }
}
