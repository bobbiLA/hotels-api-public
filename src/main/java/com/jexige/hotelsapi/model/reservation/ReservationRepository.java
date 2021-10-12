package com.jexige.hotelsapi.model.reservation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("select r from Reservation r where " +
            "r.id != :id AND " +
//            "r.client.id = :clientId AND " +
            "r.hotel.id = :hotelId AND " +
            "r.roomId = :roomId AND " +
            "( " +
            "( r.startDate >= :startDate AND r.startDate <= :endDate ) OR " +
            "( :endDate >= r.startDate AND :endDate <= r.endDate ) OR " +
            "( :startDate >= r.startDate AND :startDate <= r.endDate) OR " +
            "( r.endDate >= :startDate AND r.endDate <= :endDate )" +
            " )")
    List<Reservation> findOverlappingReservations(
            @Param("id") long id,
//            @Param("clientId") long clientId,
            @Param("hotelId") long hotelId,
            @Param("startDate") OffsetDateTime startDate,
            @Param("endDate") OffsetDateTime endDate,
            @Param("roomId") String roomId
    );
}
