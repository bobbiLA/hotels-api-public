package com.jexige.hotelsapi.controllers.login;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class LoginRequest {

    @NotNull
    private String username;

    @NotNull
    private String password;

    @Builder
    private LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
