package com.pethospital.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GeneratePasswordHash {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        System.out.println("=== Password Hash Generator ===");
        System.out.println("admin: " + encoder.encode("admin"));
        System.out.println("123456: " + encoder.encode("123456"));
    }
}
