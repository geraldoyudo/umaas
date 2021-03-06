package com.gerald.umaas.domain_admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

@SpringBootApplication
public class UmaasDomainAdminApplication extends SpringBootServletInitializer{
	 @Override
	    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
	        return application.sources(UmaasDomainAdminApplication.class);
	    }
	public static void main(String[] args) {
		SpringApplication.run(UmaasDomainAdminApplication.class, args);
	}
}
