package com.jexige.hotelsapi.configs;

import com.jexige.hotelsapi.model.admin.Admin;
import com.jexige.hotelsapi.model.admin.AdminRepository;
import com.jexige.hotelsapi.model.admin.AdminService;
import com.jexige.hotelsapi.model.admin.UserRole;
import com.jexige.hotelsapi.model.client.Client;
import com.jexige.hotelsapi.model.client.ClientRepository;
import com.jexige.hotelsapi.model.hotel.Hotel;
import com.jexige.hotelsapi.model.hotel.HotelRepository;
import com.jexige.hotelsapi.model.reservation.Reservation;
import com.jexige.hotelsapi.model.reservation.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.OffsetDateTime;
import java.util.Collections;

@Configuration
public class LoadDatabase {

    @Value("${init-db-with-test-values}")
    private boolean initDbWithTestValues;

    @Autowired
    private AdminService adminService;

    @Bean
    public CommandLineRunner initDatabase(
            AdminRepository adminRepository,
            ClientRepository clientRepository,
            HotelRepository hotelRepository,
            ReservationRepository reservationRepository) {
        return args -> {
            if (adminRepository.count() == 0
                    && clientRepository.count() == 0
                    && hotelRepository.count() == 0
                    && reservationRepository.count() == 0) {

                adminService.save(Admin.builder()
                        .username("admin")
                        .password("password")
                        .roles(Collections.singletonList(UserRole.ADMIN))
                        .build()
                );

                if (initDbWithTestValues) {
                    final Client jean = clientRepository.save(Client.builder()
                            .fullName("Jean Bob")
                            .phone("0606060606")
                            .email("jean@jean.com")
                            .address("6 rue de Jean")
                            .build()
                    );

                    // https://fr.grandluxuryhotels.com/hotel/four-seasons-george-v?cur=EUR
                    final Hotel georgesV = hotelRepository.save(Hotel.builder()
                            .name("Four Seasons Hotel")
                            .stars(5)
                            .address("31, avenue George V")
                            .phone("0 800 909 856")
                            .email("no.email@email.com")
                            .city("Paris")
                            .build()
                    );

                    final OffsetDateTime now = OffsetDateTime.now();

                    reservationRepository.save(Reservation.builder()
                            .client(jean)
                            .hotel(georgesV)
                            .startDate(now.plusDays(1))
                            .endDate(now.plusDays(2))
                            .roomId("Luxe-001")
                            .build()
                    );
                }

            }

        };
    }

}
