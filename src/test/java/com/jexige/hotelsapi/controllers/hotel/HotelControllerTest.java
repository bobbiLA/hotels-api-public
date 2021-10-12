package com.jexige.hotelsapi.controllers.hotel;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.jexige.hotelsapi.configs.ApiPaths;
import com.jexige.hotelsapi.controllers.common.InvalidProps;
import com.jexige.hotelsapi.model.admin.Admin;
import com.jexige.hotelsapi.model.admin.AdminRepository;
import com.jexige.hotelsapi.model.hotel.Hotel;
import com.jexige.hotelsapi.model.hotel.HotelRepository;
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

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class HotelControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private HotelRepository hotelRepository;

    final ObjectMapper mapper = new ObjectMapper();

    private Admin admin;

    public Admin getAdmin() {
        return admin;
    }

    ////////////////////////////////////////////////////////////////
    //  SETUP
    ////////////////////////////////////////////////////////////////

    public static String path() {
        return ApiPaths.API_PATH + ApiPaths.HOTELS_PATH;
    }

    public static String pathWithId(long id) {
        return path() + "/" + id;
    }

    private Hotel saveAndGetValidTestHotel() {
        return TestUtils.saveAndGetValidTestHotel(hotelRepository);
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
        hotelRepository.deleteAll();
    }

    ////////////////////////////////////////////////////////////////
    //  TESTS
    ////////////////////////////////////////////////////////////////

    @Test
    public void getAll_returns200WithHotels() throws Exception {
        saveAndGetValidTestHotel();
        mvc.perform(get(path()).with(user(getAdmin())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.hotelList").exists());
    }

    @Test
    public void getOne_returns200WithHotel() throws Exception {
        final Hotel testHotel = saveAndGetValidTestHotel();
        mvc.perform(get(pathWithId(testHotel.getId())).with(user(getAdmin())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(String.valueOf(testHotel.getId())));
    }

    @Test
    public void getOne_whenIdDoesNotExists_returns404NotFound() throws Exception {
        mvc.perform(get(pathWithId(-1)).with(user(getAdmin())))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void postOne_whenValidRequestBody_returns201CreatedWithCreatedBody() throws Exception {
        final Hotel hotel = TestUtils.getValidTestHotel();

        mvc.perform(post(path()).with(user(getAdmin()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(hotel)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    public void postOne_whenNameIsBlank_returns400BadRequestWithInvalidProps() throws Exception {
        final Hotel hotel = TestUtils.getValidTestHotel();
        hotel.setName(null);

        mvc.perform(post(path()).with(user(getAdmin()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(hotel)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
                .andExpect(jsonPath("$." + InvalidProps.PROPERTY_NAME + "[0].name").value("name"))
                .andExpect(jsonPath("$." + InvalidProps.PROPERTY_NAME + "[0].reason").exists());
    }

    @Test
    public void postOne_whenStarsIsLessThan0_returns400BadRequestWithInvalidProps() throws Exception {
        final Hotel hotel = TestUtils.getValidTestHotel();
        hotel.setStars(-1);

        mvc.perform(post(path()).with(user(getAdmin()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(hotel)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
                .andExpect(jsonPath("$." + InvalidProps.PROPERTY_NAME + "[0].name").value("stars"))
                .andExpect(jsonPath("$." + InvalidProps.PROPERTY_NAME + "[0].reason").exists());
    }

    @Test
    public void postOne_whenStarsIsMoreThan5_returns400BadRequestWithInvalidProps() throws Exception {
        final Hotel hotel = TestUtils.getValidTestHotel();
        hotel.setStars(6);

        mvc.perform(post(path()).with(user(getAdmin()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(hotel)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
                .andExpect(jsonPath("$." + InvalidProps.PROPERTY_NAME + "[0].name").value("stars"))
                .andExpect(jsonPath("$." + InvalidProps.PROPERTY_NAME + "[0].reason").exists());
    }

    @Test
    public void postOne_whenAddressIsBlank_returns400BadRequestWithInvalidProps() throws Exception {
        final Hotel hotel = TestUtils.getValidTestHotel();
        hotel.setAddress(null);

        mvc.perform(post(path()).with(user(getAdmin()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(hotel)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
                .andExpect(jsonPath("$." + InvalidProps.PROPERTY_NAME + "[0].name").value("address"))
                .andExpect(jsonPath("$." + InvalidProps.PROPERTY_NAME + "[0].reason").exists());
    }

    @Test
    public void postOne_whenPhoneNumberIsNotValid_returns400BadRequestWithInvalidProps() throws Exception {
        final Hotel hotel = TestUtils.getValidTestHotel();
        hotel.setPhone("qww123");

        mvc.perform(post(path()).with(user(getAdmin()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(hotel)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
                .andExpect(jsonPath("$." + InvalidProps.PROPERTY_NAME + "[0].name").value("phone"))
                .andExpect(jsonPath("$." + InvalidProps.PROPERTY_NAME + "[0].reason").exists());
    }

    @Test
    public void postOne_whenEmailIsNotValid_returns400BadRequestWithInvalidProps() throws Exception {
        final Hotel hotel = TestUtils.getValidTestHotel();
        hotel.setEmail("invalid-email");

        mvc.perform(post(path()).with(user(getAdmin()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(hotel)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
                .andExpect(jsonPath("$." + InvalidProps.PROPERTY_NAME + "[0].name").value("email"))
                .andExpect(jsonPath("$." + InvalidProps.PROPERTY_NAME + "[0].reason").exists());
    }

    @Test
    public void postOne_whenCityIsBlank_returns400BadRequestWithInvalidProps() throws Exception {
        final Hotel hotel = TestUtils.getValidTestHotel();
        hotel.setCity(null);

        mvc.perform(post(path()).with(user(getAdmin()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(hotel)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
                .andExpect(jsonPath("$." + InvalidProps.PROPERTY_NAME + "[0].name").value("city"))
                .andExpect(jsonPath("$." + InvalidProps.PROPERTY_NAME + "[0].reason").exists());
    }

    @Test
    public void putOne_whenUpdatingExistingHotelWithValidRequestBody_returns200OkWithUpdatedBody() throws Exception {
        final Hotel hotel = saveAndGetValidTestHotel();

        final String newName = "Nouveau nom";
        hotel.setName(newName);

        mvc.perform(put(pathWithId(hotel.getId())).with(user(getAdmin()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(hotel)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(newName));
    }

    @Test
    public void putOne_whenValidRequestBodyButHotelDoesNotExist_returns201CreatedWithCreatedBody() throws Exception {
        final Hotel hotel = saveAndGetValidTestHotel();
        hotel.setId(-1);

        mvc.perform(put(pathWithId(hotel.getId())).with(user(getAdmin()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(hotel)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value(hotel.getName()));
    }

    @Test
    public void deleteOne_whenHotelExists_returns200Ok() throws Exception {
        final Hotel hotel = saveAndGetValidTestHotel();

        mvc.perform(delete(pathWithId(hotel.getId())).with(user(getAdmin())))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void deleteOne_whenHotelDoesNotExists_returns404NotFound() throws Exception {
        mvc.perform(delete(pathWithId(-1L)).with(user(getAdmin())))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

}
