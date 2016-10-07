package com.gerald.umaas.domain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

@SpringBootApplication
public class UmaasDomainApplication extends SpringBootServletInitializer{
	@Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(UmaasDomainApplication.class);
    }
	public static void main(String[] args) {
		SpringApplication.run(UmaasDomainApplication.class, args);
	}
}
