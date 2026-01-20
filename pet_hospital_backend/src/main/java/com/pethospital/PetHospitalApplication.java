package com.pethospital;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.pethospital.mapper")
@EnableScheduling
public class PetHospitalApplication {
    public static void main(String[] args) {
        SpringApplication.run(PetHospitalApplication.class, args);
    }
}
