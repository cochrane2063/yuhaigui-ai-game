package com.yupi.yuhaigui.database.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class User {
    private String uuid;
    private String username;
    private String passwordHash;
    private LocalDateTime createdAt;

    public User() {}

    public User(String username, String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
    }
}
