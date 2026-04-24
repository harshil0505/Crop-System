package com.cropsys.backend.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
@Service
public class EmailVerificationServiceImpl {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Async
    public void sendOtpEmail(String to, String otp) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(fromEmail);
            mailMessage.setTo(to);
            mailMessage.setSubject("Your OTP Code");
            mailMessage.setText(
                "Your OTP code is: " + otp +
                "\n\nThis OTP will expire in 5 minutes."
            );

            mailSender.send(mailMessage);
        } catch (Exception e) {
            // IMPORTANT: log this
            System.err.println("Failed to send OTP email: " + e.getMessage());
            throw new RuntimeException("Email sending failed");
        }
    }
}


