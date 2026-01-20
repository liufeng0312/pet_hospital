package com.pethospital.test;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BCryptTest {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        String password = "admin";
        String hash = "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy";
        
        System.out.println("Testing BCrypt:");
        System.out.println("Password: " + password);
        System.out.println("Hash: " + hash);
        System.out.println("Matches: " + encoder.matches(password, hash));
        
        // 生成新的hash
        String newHash = encoder.encode(password);
        System.out.println("\nNew hash for 'admin': " + newHash);
        System.out.println("New hash matches: " + encoder.matches(password, newHash));
    }
}
