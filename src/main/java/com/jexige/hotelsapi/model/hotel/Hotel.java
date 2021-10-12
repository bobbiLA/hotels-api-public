package com.jexige.hotelsapi.model.hotel;

import com.jexige.hotelsapi.controllers.common.PatternUtils;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@Entity
@NoArgsConstructor
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank
    private String name;

    @Min(0)
    @Max(5)
    private int stars;

    @NotBlank
    private String address;

    @Pattern(regexp = PatternUtils.ALLOWED_PHONE_PATTERN, message = PatternUtils.PHONE_MISMATCH_MESSAGE)
    private String phone;

    @Pattern(regexp = PatternUtils.ALLOWED_EMAIL_PATTERN, message = PatternUtils.EMAIL_MISMATCH_MESSAGE)
    private String email;

    @NotBlank
    private String city;

    ////////////////////////////////////////////////////////////////
    //  CONSTRUCTORS
    ////////////////////////////////////////////////////////////////

    @Builder
    private Hotel(String name, int stars, String address, String phone, String email, String city) {
        this.name = name;
        this.stars = stars;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.city = city;
    }
}
