package com.cropsys.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cropsys.backend.model.CropHistory;
import com.cropsys.backend.model.User;

public interface CropHistoryRepository extends JpaRepository<CropHistory,Long> {

    List<CropHistory> findAllByUser(User user);

   

    
}
