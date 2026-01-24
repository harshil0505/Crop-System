package com.cropsys.backend.Services;

import com.cropsys.backend.Dtos.CropHistoryDto;

public interface HistoryService {


   

   // List<CropHistoryDto> getHistoryByUser(Long id);

    CropHistoryDto saveCrop(Long id, CropHistoryDto cropHistoryDto);

  

  

}
