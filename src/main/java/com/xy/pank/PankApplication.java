package com.xy.pank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class PankApplication {

    public static void main(String[] args) {
        SpringApplication.run(PankApplication.class, args);
    }

}
