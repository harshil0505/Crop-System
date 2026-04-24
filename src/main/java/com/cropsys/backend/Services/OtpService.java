package com.cropsys.backend.Services;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.cropsys.backend.Repository.OtpTokenRepository;
import com.cropsys.backend.Repository.UserRepository;
import com.cropsys.backend.model.OtpToken;
import com.cropsys.backend.model.User;

import jakarta.transaction.Transactional;

@Service
public class OtpService {

    @Autowired
    private OtpTokenRepository otpTokenRepository;

    @Autowired
    private EmailVerificationServiceImpl emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    private final Random random = new SecureRandom();
    private static final int OTP_LENGTH = 6;

    @Value("${otp.resend.cooldown.seconds:60}")
    private int resendCooldownSeconds;

    @Value("${otp.max.attempts:1}")
    private int maxAttempts;

    @Value("${otp.expiration.minutes:5}")
    private long otpExpiryMinutes;


    private String generateOtp() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < OTP_LENGTH; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    

    @Transactional
    public void createAndSendOtp(String email) {

        
        if (!userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email not registered");
        }

        otpTokenRepository.findTopByEmailOrderByCreatedAtDesc(email)
                .ifPresent(prev -> {

                    boolean expired = Instant.now().isAfter(prev.getExpiresAt());

                    
                    if (!expired && !prev.isUsed()) {
                        Instant nextAllowed =
                                prev.getCreatedAt().plusSeconds(resendCooldownSeconds);

                        if (Instant.now().isBefore(nextAllowed)) {
                            throw new IllegalStateException(
                                    "Please wait before requesting a new OTP");
                        }
                    }

                    
                    prev.setUsed(true);
                    otpTokenRepository.save(prev);
                });

        String otp = generateOtp();

        OtpToken token = new OtpToken();
        token.setEmail(email);
        token.setOtpHash(passwordEncoder.encode(otp));
        token.setCreatedAt(Instant.now());
        token.setExpiresAt(
                Instant.now().plus(otpExpiryMinutes, ChronoUnit.MINUTES)
        );
        token.setAttempts(0);
        token.setUsed(false); 

        otpTokenRepository.save(token);

        emailService.sendOtpEmail(email, otp);
    }


    @Transactional
    public boolean verifyOtp(String email, String otp) {

        OtpToken token = otpTokenRepository
                .findTopByEmailOrderByCreatedAtDesc(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("Invalid or expired OTP"));

        if (token.isUsed()) {
            throw new IllegalStateException("OTP already verified");
        }

       
        if (Instant.now().isAfter(token.getExpiresAt())) {
            token.setUsed(true);
            otpTokenRepository.save(token);
            throw new IllegalStateException("OTP expired");
        }

        
        if (token.getAttempts() >= maxAttempts) {
            token.setUsed(true);
            otpTokenRepository.save(token);
            throw new IllegalStateException("OTP locked");
        }

        token.setAttempts(token.getAttempts() + 1);

      
        if (!passwordEncoder.matches(otp, token.getOtpHash())) {
            otpTokenRepository.save(token);
            throw new IllegalArgumentException("Invalid OTP");
        }

        token.setUsed(true);
        otpTokenRepository.save(token);
        return true;
    }


    @Transactional
    public void getresetPassword(
            String email,
            String newPassword,
            String confirmPassword
    ) {

        if (!newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        OtpToken token = otpTokenRepository
                .findTopByEmailOrderByCreatedAtDesc(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("OTP not verified"));

        
        if (!token.isUsed()) {
            throw new IllegalStateException("OTP not verified");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalStateException("User not found"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);


        otpTokenRepository.delete(token);
    }
}