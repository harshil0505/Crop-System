package com.cropsys.backend.Services;

import java.util.List;

import org.springframework.security.core.Authentication;

import com.cropsys.backend.Dtos.BestCropResponse;
import com.cropsys.backend.Dtos.CropHistoryDto;
import com.cropsys.backend.Dtos.CropRecommendationDto;

public interface HistoryService {


   
    BestCropResponse predictAndSave(CropRecommendationDto dto, Authentication authentication);

    List<CropHistoryDto> getAllHistory(Authentication authentication);

  

  

}
