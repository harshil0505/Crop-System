package com.cropsys.backend.Services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.cropsys.backend.Dtos.BestCropResponse;
import com.cropsys.backend.Dtos.CropHistoryDto;
import com.cropsys.backend.Dtos.CropRecommendationDto;
import com.cropsys.backend.Dtos.CropResponce;
import com.cropsys.backend.model.CropHistory;
import com.cropsys.backend.model.CropSuggestion;
import com.cropsys.backend.model.User;
import com.cropsys.backend.repository.CropHistoryRepository;
import com.cropsys.backend.repository.UserRepository;

@Service
public class HistoryServiceimpl implements HistoryService {

    @Autowired
    private  UserRepository userRepository;

    @Autowired
    private CropHistoryRepository cropHistoryRepository;

    @Autowired
    private Mlservice mlservice;



    
    




    @Override
    public BestCropResponse predictAndSave(CropRecommendationDto dto, Authentication authentication) {
      CropResponce cropResponce = mlservice.getPridiction(dto);

        if (cropResponce == null ||
            cropResponce.top_3_crops == null ||
            cropResponce.top_3_crops.isEmpty()) {
            throw new RuntimeException("Prediction failed");
        }

        CropSuggestion suggestion = cropResponce.top_3_crops.get(0);

        BestCropResponse response = new BestCropResponse();
        response.setCrop(suggestion.getCrop());
        response.setProbability(suggestion.getProbability());

        if (authentication != null &&
            authentication.getPrincipal() instanceof UserDetails) {

            String email = authentication.getName();
            User user = userRepository.findByEmail(email).orElse(null);

            if (user != null) {
                CropHistory history = new CropHistory();

                history.setN(dto.getN());
                history.setP(dto.getP());
                history.setK(dto.getK());
                history.setTemperature(dto.getTemperature());
                history.setHumidity(dto.getHumidity());
                history.setPh(dto.getPh());
                history.setRainfall(dto.getRainfall());
                history.setCrop(suggestion.getCrop());
                history.setProbability(suggestion.getProbability());
                history.setUser(user);


                cropHistoryRepository.save(history);
            }
        }

        return response;
    }


 @Override
    public List<CropHistoryDto> getAllHistory(Authentication authentication) {
        if (authentication != null &&
            authentication.getPrincipal() instanceof UserDetails) {

            String email = authentication.getName();
            User user = userRepository.findByEmail(email).orElse(null);

            List<CropHistory> histories = cropHistoryRepository.findAllByUser(user);

            return histories.stream().map(history -> {
                CropHistoryDto dto = new CropHistoryDto();
                dto.setId(history.getId());
                dto.setN(history.getN());
                dto.setP(history.getP());
                dto.setK(history.getK());
                dto.setTemperature(history.getTemperature());
                dto.setHumidity(history.getHumidity());
                dto.setPh(history.getPh());
                dto.setRainfall(history.getRainfall());
                dto.setCrop(history.getCrop());
                dto.setProbability(history.getProbability());
                dto.setDate(LocalDate.now());
                return dto;
            }).toList();

        }
    
        return List.of();
}
}