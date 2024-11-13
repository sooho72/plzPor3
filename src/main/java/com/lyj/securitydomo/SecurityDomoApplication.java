package com.lyj.securitydomo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing  // Auditing 기능 활성화
public class SecurityDomoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecurityDomoApplication.class, args);
    }

}
