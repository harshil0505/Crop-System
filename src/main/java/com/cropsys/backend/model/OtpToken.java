package com.cropsys.backend.model;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "otp_tokens",indexes = {
    @jakarta.persistence.Index(name = "idx_email", columnList = "email")
})
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OtpToken {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;


@Column(nullable = false)
private String email;


@Column(nullable = false)
private String otpHash;


@Column(nullable = false)
private Instant expiresAt;


@Column(nullable = false)
private int attempts = 0;


@Column(nullable = false)
private boolean used = false;

@Column(nullable = false, updatable = false)
private Instant createdAt = Instant.now();
}
