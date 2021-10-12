package com.jexige.hotelsapi.model.client;

import com.jexige.hotelsapi.controllers.common.PatternUtils;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@Entity
@NoArgsConstructor
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank
    private String fullName;

    @Pattern(regexp = PatternUtils.ALLOWED_PHONE_PATTERN, message = PatternUtils.PHONE_MISMATCH_MESSAGE)
    private String phone;

    @Pattern(regexp = PatternUtils.ALLOWED_EMAIL_PATTERN, message = PatternUtils.EMAIL_MISMATCH_MESSAGE)
    private String email;

    @NotBlank
    private String address;

    ////////////////////////////////////////////////////////////////
    //  CONSTRUCTORS
    ////////////////////////////////////////////////////////////////

    @Builder
    private Client(String fullName, String phone, String email, String address) {
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.address = address;
    }
}
