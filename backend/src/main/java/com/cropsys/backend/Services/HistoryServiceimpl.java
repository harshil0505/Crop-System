package com.cropsys.backend.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cropsys.backend.Dtos.CropHistoryDto;
import com.cropsys.backend.Repository.CropHistoryRepository;
import com.cropsys.backend.Repository.UserRepository;
import com.cropsys.backend.model.CropHistory;
import com.cropsys.backend.model.User;

@Service
public class HistoryServiceimpl implements HistoryService {

    @Autowired
    private  UserRepository userRepository;

    @Autowired
    private CropHistoryRepository cropHistoryRepository;




    
    @Override
    public CropHistoryDto saveCrop(Long id, CropHistoryDto dto) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // DTO → ENTITY
        CropHistory history = new CropHistory();
        history.setN(dto.getN());
        history.setP(dto.getP());
        history.setK(dto.getK());
        history.setHumidity(dto.getHumidity());
        history.setTemperature(dto.getTemperature());
        history.setPh(dto.getPh());
        history.setRainfall(dto.getRainfall());
        history.setCrop(dto.getCrop());
        history.setProbability(dto.getProbability());
        history.setUser(user);

        CropHistory savedHistory = cropHistoryRepository.save(history);

        // ENTITY → DTO
        dto.setId(savedHistory.getId());
        return dto;
    }

   
}
