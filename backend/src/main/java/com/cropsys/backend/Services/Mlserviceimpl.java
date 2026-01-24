package com.cropsys.backend.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.cropsys.backend.Dtos.CropRecommendationDto;
import com.cropsys.backend.Dtos.CropResponce;

@Service
public class Mlserviceimpl  implements Mlservice{

    @Autowired
    private RestTemplate restTemplate;
   
    @Override
    public CropResponce getPridiction(CropRecommendationDto cropRecommendationDto) {
      
        String flaskUrl="http://127.0.0.1:5000/predict";
        return restTemplate.postForObject(flaskUrl, cropRecommendationDto, CropResponce.class);
    }

}
 