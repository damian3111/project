package com.example.project.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class PasswordResetToken {

    private static final int EXPIRATION = 60 * 24;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    @OneToOne(targetEntity = AppUser.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private AppUser appUser;

    private LocalDateTime expiryDate;

    public PasswordResetToken(String token, AppUser appUser, LocalDateTime expiryDate) {
        this.token = token;
        this.appUser = appUser;
        this.expiryDate = expiryDate;
    }
}
