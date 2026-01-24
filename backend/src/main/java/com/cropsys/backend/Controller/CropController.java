package com.cropsys.backend.Controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.cropsys.backend.Dtos.BestCropResponse;
import com.cropsys.backend.Dtos.CropHistoryDto;
import com.cropsys.backend.Dtos.CropRecommendationDto;
import com.cropsys.backend.Dtos.CropResponce;
import com.cropsys.backend.Repository.CropHistoryRepository;
import com.cropsys.backend.Repository.UserRepository;
import com.cropsys.backend.Services.HistoryService;
import com.cropsys.backend.Services.Mlservice;
import com.cropsys.backend.model.CropHistory;
import com.cropsys.backend.model.CropSuggestion;
import com.cropsys.backend.model.User;

@RestController
@RequestMapping("/api/crop")
public class CropController {

 
    @Autowired
    private CropHistoryRepository cropHistoryRepository;

    @Autowired
    private Mlservice mlservice;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private UserRepository userRepository;

  

    @PostMapping("/predict")
public ResponseEntity<BestCropResponse> predictCrop(
        @RequestBody CropRecommendationDto cropRecommendationDto,
        Authentication authentication) {

    if (authentication == null || !authentication.isAuthenticated()) {
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
    }

    // 🔑 Extract username/email from JWT
    String email = authentication.getName();

    User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "User not found"));
                   


    CropResponce cropResponce = mlservice.getPridiction(cropRecommendationDto);

    if (cropResponce == null ||
        cropResponce.top_3_crops == null ||
        cropResponce.top_3_crops.isEmpty()) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No crop found");
    }

    CropSuggestion cropSuggestion = cropResponce.top_3_crops.get(0);

    CropHistory history = new CropHistory();
    history.setN(cropRecommendationDto.getN());
    history.setP(cropRecommendationDto.getP());
    history.setK(cropRecommendationDto.getK());
    history.setHumidity(cropRecommendationDto.getHumidity());
    history.setTemperature(cropRecommendationDto.getTemperature());
    history.setPh(cropRecommendationDto.getPh());
    history.setRainfall(cropRecommendationDto.getRainfall());
    history.setCrop(cropSuggestion.getCrop());
    history.setProbability(cropSuggestion.getProbability());

    // ✅ THIS is the missing line
    history.setUser(user);

    CropHistory saved = cropHistoryRepository.save(history);

    BestCropResponse response = new BestCropResponse();
    response.setCrop(saved.getCrop());
    response.setProbability(saved.getProbability());

    return ResponseEntity.ok(response);
}

    

    @PostMapping("/{id}/history")
    public ResponseEntity<CropHistoryDto> saveCrop(@PathVariable Long id,@RequestBody CropHistoryDto cropHistoryDto) {
    CropHistoryDto savedHistory =historyService.saveCrop(id, cropHistoryDto);

    return ResponseEntity.status(HttpStatus.CREATED).body(savedHistory);
}
 



 


}
