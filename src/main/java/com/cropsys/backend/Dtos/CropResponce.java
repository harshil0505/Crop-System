package com.cropsys.backend.Dtos;

import java.util.List;

import com.cropsys.backend.model.CropSuggestion;

public class CropResponce {
    public String recommended_crop;
    public double confidence_percent;
    public List<CropSuggestion> top_3_crops;
}
