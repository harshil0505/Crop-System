package com.cropsys.backend.Dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CropRecommendationDto {
    private Long id;
    @JsonProperty("N")
    private Integer N;

    @JsonProperty("P")
    private Integer P;

    @JsonProperty("K")
    private Integer K;
    
    @JsonProperty("humidity")
    private Integer humidity;

    @JsonProperty("temperature")
    private Integer temperature;

    @JsonProperty("ph")
    private Double ph;

    @JsonProperty("rainfall")
    private Integer rainfall;
}
