package com.gerald.umaas.registration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class UmaasRegistrationApplication {

	public static void main(String[] args) {
		SpringApplication.run(UmaasRegistrationApplication.class, args);
	}
}
