package com.cropsys.backend.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cropsys.backend.model.CropHistory;

public interface CropHistoryRepository extends JpaRepository<CropHistory,Long> {

   

    
}
