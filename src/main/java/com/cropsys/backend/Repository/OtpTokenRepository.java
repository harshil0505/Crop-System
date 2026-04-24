package com.cropsys.backend.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cropsys.backend.model.OtpToken;



public interface OtpTokenRepository extends JpaRepository<OtpToken, Long> {

    Optional<OtpToken>  findTopByEmailOrderByCreatedAtDesc(String email);
    
    void deleteByEmail(String email);
}
