package com.pix.xcwebserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
public class XcWebServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(XcWebServerApplication.class, args);
    }

}

