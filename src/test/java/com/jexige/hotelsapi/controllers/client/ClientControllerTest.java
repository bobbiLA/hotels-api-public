package com.jexige.hotelsapi.controllers.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jexige.hotelsapi.configs.ApiPaths;
import com.jexige.hotelsapi.controllers.common.InvalidProps;
import com.jexige.hotelsapi.model.admin.Admin;
import com.jexige.hotelsapi.model.admin.AdminRepository;
import com.jexige.hotelsapi.model.client.Client;
import com.jexige.hotelsapi.model.client.ClientRepository;
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
public class ClientControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private ClientRepository clientRepository;

    final ObjectMapper mapper = new ObjectMapper();

    private Admin admin;

    public Admin getAdmin() {
        return admin;
    }

    ////////////////////////////////////////////////////////////////
    //  SETUP
    ////////////////////////////////////////////////////////////////

    public static String path() {
        return ApiPaths.API_PATH + ApiPaths.CLIENTS_PATH;
    }

    public static String pathWithId(long id) {
        return path() + "/" + id;
    }

    private Client saveAndGetValidTestClient() {
        return TestUtils.saveAndGetValidTestClient(clientRepository);
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
        clientRepository.deleteAll();
    }

    ////////////////////////////////////////////////////////////////
    //  TESTS
    ////////////////////////////////////////////////////////////////

    @Test
    public void getAll_returns200WithClients() throws Exception {
        saveAndGetValidTestClient();
        mvc.perform(get(path()).with(user(getAdmin())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.clientList").exists());
    }

    @Test
    public void getOne_returns200WithClient() throws Exception {
        final Client testClient = saveAndGetValidTestClient();
        mvc.perform(get(pathWithId(testClient.getId())).with(user(getAdmin())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(String.valueOf(testClient.getId())));
    }

    @Test
    public void getOne_whenIdDoesNotExists_returns404NotFound() throws Exception {
        mvc.perform(get(pathWithId(-1)).with(user(getAdmin())))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void postOne_whenValidRequestBody_returns201CreatedWithCreatedBody() throws Exception {
        final Client client = TestUtils.getValidTestClient();

        mvc.perform(post(path()).with(user(getAdmin()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(client)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    public void postOne_whenFullNameIsBlank_returns400BadRequestWithInvalidProps() throws Exception {
        final Client client = TestUtils.getValidTestClient();
        client.setFullName(null);

        mvc.perform(post(path()).with(user(getAdmin()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(client)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
                .andExpect(jsonPath("$." + InvalidProps.PROPERTY_NAME + "[0].name").value("fullName"))
                .andExpect(jsonPath("$." + InvalidProps.PROPERTY_NAME + "[0].reason").exists());
    }

    @Test
    public void postOne_whenPhoneIsNotValid_returns400BadRequestWithInvalidProps() throws Exception {
        final Client client = TestUtils.getValidTestClient();
        client.setPhone("qqc1xw56");

        mvc.perform(post(path()).with(user(getAdmin()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(client)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
                .andExpect(jsonPath("$." + InvalidProps.PROPERTY_NAME + "[0].name").value("phone"))
                .andExpect(jsonPath("$." + InvalidProps.PROPERTY_NAME + "[0].reason").exists());
    }

    @Test
    public void postOne_whenEmailIsNotValid_returns400BadRequestWithInvalidProps() throws Exception {
        final Client client = TestUtils.getValidTestClient();
        client.setEmail("invalid-email");

        mvc.perform(post(path()).with(user(getAdmin()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(client)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
                .andExpect(jsonPath("$." + InvalidProps.PROPERTY_NAME + "[0].name").value("email"))
                .andExpect(jsonPath("$." + InvalidProps.PROPERTY_NAME + "[0].reason").exists());
    }

    @Test
    public void postOne_whenAddressIsBlank_returns400BadRequestWithInvalidProps() throws Exception {
        final Client client = TestUtils.getValidTestClient();
        client.setAddress(null);

        mvc.perform(post(path()).with(user(getAdmin()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(client)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
                .andExpect(jsonPath("$." + InvalidProps.PROPERTY_NAME + "[0].name").value("address"))
                .andExpect(jsonPath("$." + InvalidProps.PROPERTY_NAME + "[0].reason").exists());
    }

    @Test
    public void putOne_whenUpdatingExistingClientWithValidRequestBody_returns200OkWithUpdatedBody() throws Exception {
        final Client client = saveAndGetValidTestClient();

        final String newFullName = "Mathilde Lacroix";
        client.setFullName(newFullName);

        mvc.perform(put(pathWithId(client.getId())).with(user(getAdmin()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(client)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value(newFullName));
    }

    @Test
    public void putOne_whenValidRequestBodyButClientDoesNotExist_returns201CreatedWithCreatedBody() throws Exception {
        final Client client = saveAndGetValidTestClient();
        client.setId(-1);

        mvc.perform(put(pathWithId(client.getId())).with(user(getAdmin()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(client)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.fullName").value(client.getFullName()));
    }

    @Test
    public void deleteOne_whenClientExists_returns200Ok() throws Exception {
        final Client client = saveAndGetValidTestClient();

        mvc.perform(delete(pathWithId(client.getId())).with(user(getAdmin())))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void deleteOne_whenClientDoesNotExists_returns404NotFound() throws Exception {
        mvc.perform(delete(pathWithId(-1L)).with(user(getAdmin())))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

}
