package com.jexige.hotelsapi.controllers.login;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jexige.hotelsapi.configs.ApiPaths;
import com.jexige.hotelsapi.controllers.common.InvalidProps;
import com.jexige.hotelsapi.model.admin.Admin;
import com.jexige.hotelsapi.model.admin.AdminRepository;
import com.jexige.hotelsapi.model.admin.AdminService;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class LoginControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private AdminService adminService;

    final ObjectMapper mapper = new ObjectMapper();

    ////////////////////////////////////////////////////////////////
    //  SETUP
    ////////////////////////////////////////////////////////////////

    public static String path() {
        return ApiPaths.LOGIN_PATH;
    }

    @BeforeEach
    @AfterEach
    public void cleanUp() {
        clearAllRepositories();
    }

    private void clearAllRepositories() {
        adminRepository.deleteAll();
    }

    ////////////////////////////////////////////////////////////////
    //  TESTS
    ////////////////////////////////////////////////////////////////

    @Test
    public void postLogin_whenValidCredentials_returns200Ok() throws Exception {
        final Admin admin = TestUtils.getValidAdmin();
        final String clearTextPassword = admin.getPassword();
        adminService.save(admin);

        final LoginRequest loginRequest = LoginRequest.builder()
                .username(admin.getUsername())
                .password(clearTextPassword)
                .build();

        mvc.perform(post(path())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void postLogin_whenBlankUsername_returns400BadRequestWithInvalidProps() throws Exception {
        final LoginRequest loginRequest = LoginRequest.builder()
                .username(null)
                .password("anyPassword")
                .build();

        mvc.perform(post(path())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
                .andExpect(jsonPath("$." + InvalidProps.PROPERTY_NAME + "[0].name").value("username"))
                .andExpect(jsonPath("$." + InvalidProps.PROPERTY_NAME + "[0].reason").exists());
    }

    @Test
    public void postLogin_whenBlankPassword_returns400BadRequestWithInvalidProps() throws Exception {
        final LoginRequest loginRequest = LoginRequest.builder()
                .username("anyUsername")
                .password(null)
                .build();

        mvc.perform(post(path())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
                .andExpect(jsonPath("$." + InvalidProps.PROPERTY_NAME + "[0].name").value("password"))
                .andExpect(jsonPath("$." + InvalidProps.PROPERTY_NAME + "[0].reason").exists());
    }

    @Test
    public void postLogin_whenSpecifiedButInvalidCredentials_returns422UnprocessableEntityWithDetail() throws Exception {
        final Admin admin = TestUtils.getValidAdmin();
        final String invalidPassword = admin.getPassword() + "XYZ";
        adminService.save(admin);

        final LoginRequest loginRequest = LoginRequest.builder()
                .username(admin.getUsername())
                .password(invalidPassword)
                .build();

        mvc.perform(post(path())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.detail").isNotEmpty());
    }

}
