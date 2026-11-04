package com.karigarnow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class KarigarNowApplication {
    public static void main(String[] args) {
        SpringApplication.run(KarigarNowApplication.class, args);
    }
}
