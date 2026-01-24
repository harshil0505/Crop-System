package com.cropsys.backend.Dtos;

import lombok.Data;

@Data
public class CropHistoryDto {

    
    private Long id;
    private Integer N;
    private Integer P;
    private Integer K;
    private Integer humidity;
    private Integer temperature;
    private Double ph;
    private Integer rainfall;

    private String crop;
    private Double probability;

}
