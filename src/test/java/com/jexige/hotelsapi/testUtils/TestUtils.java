package com.jexige.hotelsapi.testUtils;

import com.jexige.hotelsapi.model.admin.Admin;
import com.jexige.hotelsapi.model.admin.AdminRepository;
import com.jexige.hotelsapi.model.admin.UserRole;
import com.jexige.hotelsapi.model.client.Client;
import com.jexige.hotelsapi.model.client.ClientRepository;
import com.jexige.hotelsapi.model.hotel.Hotel;
import com.jexige.hotelsapi.model.hotel.HotelRepository;
import com.jexige.hotelsapi.model.reservation.Reservation;
import com.jexige.hotelsapi.model.reservation.ReservationRepository;

import java.time.OffsetDateTime;
import java.util.Collections;

public class TestUtils {

    public static Admin saveAndGetValidAdmin(AdminRepository adminRepository) {
        return adminRepository.save(getValidAdmin());
    }

    public static Admin getValidAdmin() {
        return Admin.builder()
                .username("admin")
                .password("password")
                .roles(Collections.singletonList(UserRole.ADMIN))
                .build();
    }

    public static Client saveAndGetValidTestClient(ClientRepository clientRepository) {
        return clientRepository.save(getValidTestClient());
    }

    public static Client getValidTestClient() {
        return Client.builder()
                .fullName("Jeane Leroy")
                .phone("06 60 600 600")
                .email("jeane@leroy.com")
                .address("10 rue Henri Poincare, 13013 Marseille")
                .build();
    }

    public static Hotel saveAndGetValidTestHotel(HotelRepository hotelRepository) {
        return hotelRepository.save(getValidTestHotel());
    }

    public static Hotel getValidTestHotel() {
        return Hotel.builder()
                .name("Four Seasons Hotel")
                .stars(5)
                .address("31, avenue George V")
                .phone("0 800 909 856")
                .email("no.email@email.com")
                .city("Paris")
                .build();
    }

    public static Reservation saveAndGetValidTestReservation(ReservationRepository reservationRepository, Client client, Hotel hotel) {
        return reservationRepository.save(getValidTestReservation(client, hotel));
    }

    public static Reservation getValidTestReservation(Client client, Hotel hotel) {
        return Reservation.builder()
                .client(client)
                .hotel(hotel)
                .startDate(OffsetDateTime.now().plusDays(1))
                .endDate(OffsetDateTime.now().plusDays(2))
                .roomId("SR-001")
                .build();
    }
}
