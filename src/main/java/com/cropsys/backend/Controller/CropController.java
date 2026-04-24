package com.cropsys.backend.Controller;

import org.springframework.security.core.Authentication;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.cropsys.backend.Dtos.BestCropResponse;
import com.cropsys.backend.Dtos.CropHistoryDto;
import com.cropsys.backend.Dtos.CropRecommendationDto;
import com.cropsys.backend.Services.HistoryService;

@RestController
@RequestMapping("/api/crop")
public class CropController {

 
    @Autowired
    private HistoryService historyService;

    @PostMapping("/predict")
    public ResponseEntity<BestCropResponse> predictCrop(
            @RequestBody CropRecommendationDto dto,
            Authentication authentication) {
    
        BestCropResponse response =
                historyService.predictAndSave(dto, authentication);
    
        return ResponseEntity.ok(response);
    }
    
  
    @GetMapping("/history")
    public ResponseEntity<List<CropHistoryDto>> getAllHistory(
            Authentication authentication) {
    
        List<CropHistoryDto> history =
                (List<CropHistoryDto>) historyService.getAllHistory(authentication);
    
        return ResponseEntity.ok(history);
    }
    
    
 
}