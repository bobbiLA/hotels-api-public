package com.jexige.hotelsapi.controllers.common;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class InvalidProps {
    public static final String PROPERTY_NAME = "invalid-props";
    private String name;
    private String reason;
}