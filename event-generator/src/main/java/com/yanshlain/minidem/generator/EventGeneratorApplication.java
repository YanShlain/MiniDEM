package com.yanshlain.minidem.generator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

// @EnableScheduling turns on Spring's scheduling machinery app-wide -- without it,
// @Scheduled methods anywhere in the app (EventGeneratorService's tick) are simply
// never invoked, silently. It's an opt-in switch, not automatic just from the
// annotation existing on a method.
@SpringBootApplication
@EnableScheduling
public class EventGeneratorApplication {

    public static void main(String[] args) {
        SpringApplication.run(EventGeneratorApplication.class, args);
    }

}
