package com.jexige.hotelsapi.controllers.reservation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jexige.hotelsapi.configs.ApiPaths;
import com.jexige.hotelsapi.controllers.common.InvalidProps;
import com.jexige.hotelsapi.model.admin.Admin;
import com.jexige.hotelsapi.model.admin.AdminRepository;
import com.jexige.hotelsapi.model.client.Client;
import com.jexige.hotelsapi.model.client.ClientRepository;
import com.jexige.hotelsapi.model.hotel.Hotel;
import com.jexige.hotelsapi.model.hotel.HotelRepository;
import com.jexige.hotelsapi.model.reservation.Reservation;
import com.jexige.hotelsapi.model.reservation.ReservationRepository;
import com.jexige.hotelsapi.testUtils.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ReservationControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    final ObjectMapper mapper = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .build();

    private Admin admin;

    public Admin getAdmin() {
        return admin;
    }

    ////////////////////////////////////////////////////////////////
    //  SETUP
    ////////////////////////////////////////////////////////////////

    public static String path() {
        return ApiPaths.API_PATH + ApiPaths.RESERVATIONS_PATH;
    }

    public static String pathWithId(long id) {
        return path() + "/" + id;
    }

    private Client saveAndGetValidTestClient() {
        return TestUtils.saveAndGetValidTestClient(clientRepository);
    }

    private Hotel saveAndGetValidTestHotel() {
        return TestUtils.saveAndGetValidTestHotel(hotelRepository);
    }

    private Reservation saveAndGetValidTestReservation(Client client, Hotel hotel) {
        return TestUtils.saveAndGetValidTestReservation(reservationRepository, client, hotel);
    }

    @BeforeEach
    public void beforeEach() {
        clearAllRepositories();
        initAdmin();
    }

    @AfterEach
    public void afterEach() {
        clearAllRepositories();
    }

    private void initAdmin() {
        this.admin = TestUtils.saveAndGetValidAdmin(adminRepository);
    }

    private void clearAllRepositories() {
        adminRepository.deleteAll();
        reservationRepository.deleteAll();
        clientRepository.deleteAll();
        hotelRepository.deleteAll();
    }

    ////////////////////////////////////////////////////////////////
    //  TESTS
    ////////////////////////////////////////////////////////////////

    @Test
    public void getAll_returns200OkWithReservations() throws Exception {
        saveAndGetValidTestReservation(saveAndGetValidTestClient(), saveAndGetValidTestHotel());

        mvc.perform(get(path()).with(user(getAdmin())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.reservationList").exists());
    }

    @Test
    public void getOne_returns200OkWithReservation() throws Exception {
        final Reservation testReservation = saveAndGetValidTestReservation(saveAndGetValidTestClient(), saveAndGetValidTestHotel());
        mvc.perform(get(pathWithId(testReservation.getId())).with(user(getAdmin())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(String.valueOf(testReservation.getId())));
    }

    @Test
    public void getOne_whenIdDoesNotExists_returns404NotFound() throws Exception {
        mvc.perform(get(pathWithId(-1)).with(user(getAdmin())))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void postOne_whenValidRequestBody_returns201CreatedWithCreatedBody() throws Exception {
        final Reservation testReservation = TestUtils.getValidTestReservation(saveAndGetValidTestClient(), saveAndGetValidTestHotel());

        mvc.perform(post(path()).with(user(getAdmin()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(testReservation)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    public void postOne_whenStartDateIsNull_returns400BadRequestWithInvalidProps() throws Exception {
        final Reservation testReservation = TestUtils.getValidTestReservation(saveAndGetValidTestClient(), saveAndGetValidTestHotel());
        testReservation.setStartDate(null);

        mvc.perform(post(path()).with(user(getAdmin()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(testReservation)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
                .andExpect(jsonPath("$." + InvalidProps.PROPERTY_NAME + "[0].name").value("startDate"))
                .andExpect(jsonPath("$." + InvalidProps.PROPERTY_NAME + "[0].reason").exists());
    }

    @Test
    public void postOne_whenEndDateIsNull_returns400BadRequestWithInvalidProps() throws Exception {
        final Reservation testReservation = TestUtils.getValidTestReservation(saveAndGetValidTestClient(), saveAndGetValidTestHotel());
        testReservation.setEndDate(null);

        mvc.perform(post(path()).with(user(getAdmin()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(testReservation)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
                .andExpect(jsonPath("$." + InvalidProps.PROPERTY_NAME + "[0].name").value("endDate"))
                .andExpect(jsonPath("$." + InvalidProps.PROPERTY_NAME + "[0].reason").exists());
    }

    @Test
    public void postOne_whenEndDateIsBeforeStartDate_returns400BadRequestWithInvalidProps() throws Exception {
        final Reservation testReservation = TestUtils.getValidTestReservation(saveAndGetValidTestClient(), saveAndGetValidTestHotel());
        testReservation.setStartDate(OffsetDateTime.now().plusDays(2));
        testReservation.setEndDate(OffsetDateTime.now().plusDays(1));

        mvc.perform(post(path()).with(user(getAdmin()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(testReservation)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
                .andExpect(jsonPath("$." + InvalidProps.PROPERTY_NAME + "[0].name").value("reservation"))
                .andExpect(jsonPath("$." + InvalidProps.PROPERTY_NAME + "[0].reason").exists());
    }

    @Test
    public void postOne_whenRoomIdIsBlank_returns400BadRequestWithInvalidProps() throws Exception {
        final Reservation testReservation = TestUtils.getValidTestReservation(saveAndGetValidTestClient(), saveAndGetValidTestHotel());
        testReservation.setRoomId(null);

        mvc.perform(post(path()).with(user(getAdmin()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(testReservation)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
                .andExpect(jsonPath("$." + InvalidProps.PROPERTY_NAME + "[0].name").value("roomId"))
                .andExpect(jsonPath("$." + InvalidProps.PROPERTY_NAME + "[0].reason").exists());

    }

    @Test
    public void postOne_whenOverlappingReservationOnTheSameRoom_returns422UnprocessableEntityWithInvalidProps() throws Exception {
        final Client validTestClient = saveAndGetValidTestClient();
        final Hotel validTestHotel = saveAndGetValidTestHotel();

        final OffsetDateTime date1 = OffsetDateTime.now().plusDays(1);
        final OffsetDateTime date2 = OffsetDateTime.now().plusDays(2);
        final OffsetDateTime date3 = OffsetDateTime.now().plusDays(3);
        final OffsetDateTime date4 = OffsetDateTime.now().plusDays(4);

        final String roomId = "SR-001";

        reservationRepository.save(
                Reservation.builder()
                        .client(validTestClient)
                        .hotel(validTestHotel)
                        .startDate(date1)
                        .endDate(date3)
                        .roomId(roomId)
                        .build()
        );

        final Reservation overlappingReservation =
                Reservation.builder()
                        .client(validTestClient)
                        .hotel(validTestHotel)
                        .startDate(date2)
                        .endDate(date4)
                        .roomId(roomId)
                        .build();

        mvc.perform(post(path()).with(user(getAdmin()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(overlappingReservation)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.title").value(HttpStatus.UNPROCESSABLE_ENTITY.getReasonPhrase()))
                .andExpect(jsonPath("$.detail").isNotEmpty());
    }

    @Test
    public void putOne_whenUpdatingExistingReservationWithValidRequestBody_returns200OkWithUpdatedBody() throws Exception {
        final Reservation testReservation = saveAndGetValidTestReservation(saveAndGetValidTestClient(), saveAndGetValidTestHotel());

        testReservation.setEndDate(testReservation.getEndDate().plusHours(1));

        mvc.perform(put(pathWithId(testReservation.getId())).with(user(getAdmin()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(testReservation)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void putOne_whenValidRequestBodyButReservationDoesNotExist_returns201CreatedWithCreatedBody() throws Exception {
        final Reservation testReservation = TestUtils.getValidTestReservation(saveAndGetValidTestClient(), saveAndGetValidTestHotel());
        testReservation.setId(-1);

        mvc.perform(put(pathWithId(testReservation.getId())).with(user(getAdmin()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(testReservation)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    public void deleteOne_whenReservationExists_returns200Ok() throws Exception {
        final Reservation testReservation = saveAndGetValidTestReservation(saveAndGetValidTestClient(), saveAndGetValidTestHotel());

        mvc.perform(delete(pathWithId(testReservation.getId())).with(user(getAdmin())))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void deleteOne_whenReservationDoesNotExist_returns404NotFound() throws Exception {
        mvc.perform(delete(pathWithId(-1L)).with(user(getAdmin())))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
    
}
