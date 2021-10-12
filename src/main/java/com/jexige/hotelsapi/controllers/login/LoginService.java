package com.jexige.hotelsapi.controllers.login;

import com.jexige.hotelsapi.model.admin.Admin;
import com.jexige.hotelsapi.model.admin.AdminRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoginService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    public LoginService(AdminRepository adminRepository, PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void validateLogin(String requestUsername, String requestPassword) throws RejectedLoginException {
        final Optional<Admin> opt = adminRepository.findByUsername(requestUsername);

        if (opt.isEmpty() || !passwordEncoder.matches(requestPassword, opt.get().getPassword())) {
            throw new RejectedLoginException();
        }
    }
}
