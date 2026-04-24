package com.cropsys.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "crop_recommendation")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CropRecommendation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer N;
    private Integer P;
    private Integer K;
    private Integer humidity;
    private Integer temperature;
    private Double ph;
    private Integer rainfall;

    
}
