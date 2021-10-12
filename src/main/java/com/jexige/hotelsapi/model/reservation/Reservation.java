package com.jexige.hotelsapi.model.reservation;

import com.jexige.hotelsapi.model.client.Client;
import com.jexige.hotelsapi.model.hotel.Hotel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;

@Data
@Entity
@NoArgsConstructor
@ValidReservationDates
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "client_id", foreignKey = @ForeignKey(name = "CLIENT_ID_FK_1"))
    private Client client;

    @ManyToOne
    @JoinColumn(name = "hotel_id", foreignKey = @ForeignKey(name = "HOTEL_ID_FK_1"))
    private Hotel hotel;

    @NotNull
    private OffsetDateTime startDate;

    @NotNull
    private OffsetDateTime endDate;

    @NotBlank
    private String roomId;

    ////////////////////////////////////////////////////////////////
    //  CONSTRUCTORS
    ////////////////////////////////////////////////////////////////

    @Builder
    private Reservation(Client client, Hotel hotel, OffsetDateTime startDate, OffsetDateTime endDate, String roomId) {
        this.client = client;
        this.hotel = hotel;
        this.startDate = startDate;
        this.endDate = endDate;
        this.roomId = roomId;
    }

}
