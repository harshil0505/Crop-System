package com.cropsys.backend.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Getter;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CropSuggestion {
    
    private String crop;
    private Double probability;
}
