package com.spotprice.domain.user;

import java.time.Instant;

public class User {

    private Long id;
    private String email;
    private String passwordHash;
    private Instant createdAt;

    public User(String email, String passwordHash, Instant createdAt) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("이메일은 필수입니다.");
        }
        if (passwordHash == null || passwordHash.isBlank()) {
            throw new IllegalArgumentException("비밀번호 해시는 필수입니다.");
        }

        this.email = email;
        this.passwordHash = passwordHash;
        this.createdAt = createdAt;
    }

    public static User restore(Long id, String email, String passwordHash, Instant createdAt) {
        User user = new User();
        user.id = id;
        user.email = email;
        user.passwordHash = passwordHash;
        user.createdAt = createdAt;
        return user;
    }

    private User() {
    }

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public Instant getCreatedAt() { return createdAt; }
}
