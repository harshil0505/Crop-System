package com.cropsys.backend.Services;

import com.cropsys.backend.Dtos.CropRecommendationDto;
import com.cropsys.backend.Dtos.CropResponce;

public interface Mlservice {

    CropResponce getPridiction(CropRecommendationDto cropRecommendationDto);

}
