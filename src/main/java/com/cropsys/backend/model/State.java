package com.cropsys.backend.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum State {

    GUJARAT("Gujarat"),
    MAHARASHTRA("Maharashtra"),
    RAJASTHAN("Rajasthan"),
    TAMIL_NADU("Tamil Nadu"),
    KARNATAKA("Karnataka"),
    KERALA("Kerala"),
    TELANGANA("Telangana"),
    ANDHRA_PRADESH("Andhra Pradesh"),
    MADHYA_PRADESH("Madhya Pradesh"),
    UTTAR_PRADESH("Uttar Pradesh"),
    WEST_BENGAL("West Bengal"),
    BIHAR("Bihar"),
    GOA("Goa"),
    PUNJAB("Punjab"),
    HARYANA("Haryana"),
    ASSAM("Assam"),
    ODISHA("Odisha"),
    JHARKHAND("Jharkhand"),
    CHHATTISGARH("Chhattisgarh"),
    HIMACHAL_PRADESH("Himachal Pradesh"),
    UTTARAKHAND("Uttarakhand"),
    MEGHALAYA("Meghalaya"),
    MANIPUR("Manipur"),
    MIZORAM("Mizoram"),
    NAGALAND("Nagaland"),
    SIKKIM("Sikkim"),
    TRIPURA("Tripura"),
    ARUNACHAL_PRADESH("Arunachal Pradesh");

    private final String displayName;

    State(String displayName) {
        this.displayName = displayName;
    }

    @JsonValue
    public String getDisplayName() {
        return displayName;
    }


    @JsonCreator
    public static State from(String value) {
        return Arrays.stream(State.values())
                .filter(state ->
                        state.displayName.equalsIgnoreCase(value) ||
                        state.name().equalsIgnoreCase(value.replace(" ", "_"))
                )
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("Invalid state: " + value)
                );
    }
}
