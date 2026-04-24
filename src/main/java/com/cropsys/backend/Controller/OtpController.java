package com.cropsys.backend.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cropsys.backend.Dtos.OtpRequestDto;
import com.cropsys.backend.Dtos.OtpVerifyDto;
import com.cropsys.backend.Dtos.ResetPasswordDto;
import com.cropsys.backend.Services.OtpService;




@RestController
@RequestMapping("/api/otp")
public class OtpController {

    @Autowired
    private OtpService otpService;

    @PostMapping("/forgot-password")

    public ResponseEntity<?> requestOtp(@Validated @RequestBody OtpRequestDto dto,Authentication authentication) {

        
        try {
            otpService.createAndSendOtp(dto.getEmail());
            return ResponseEntity.ok("OTP sent to email.");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send OTP.");
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@Validated @RequestBody OtpVerifyDto dto) {
        try {
            boolean ok = otpService.verifyOtp(dto.getEmail(), dto.getOtp());
            if (ok)
                return ResponseEntity.ok("OTP verified.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid OTP.");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Verification failed.");
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> getresetPassword(@RequestBody ResetPasswordDto dto) {
                 otpService.getresetPassword(dto.getEmail(), dto.getNewPassword(), dto.getConfirmPassword());    
        return ResponseEntity.ok("Password reset successful.");
    
    }
}
