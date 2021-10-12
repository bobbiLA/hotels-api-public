package com.jexige.hotelsapi.model.admin;

import com.jexige.hotelsapi.testUtils.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class AdminServiceTest {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private AdminService adminService;

    @BeforeEach
    @AfterEach
    public void cleanUp() {
        clearAllRepositories();
    }

    private void clearAllRepositories() {
        adminRepository.deleteAll();
    }

    @Test
    public void save_whenUserIsValid_passwordIsPersistedEncrypted() {
        final Admin admin = TestUtils.getValidAdmin();
        final String clearTextPassword = admin.getPassword();
        final Admin savedAdmin = adminService.save(admin);
        assertThat(savedAdmin.getPassword()).isNotEqualTo(clearTextPassword);
    }

}
