package com.gerald.umaas.domain_admin.config;

import org.apache.camel.component.servlet.CamelHttpTransportServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.DispatcherServlet;


@Configuration
public class CamelConfig {
	private static final String CAMEL_URL_MAPPING = "/umaas/*";
	private static final String CAMEL_SERVLET_NAME = "CamelServlet";
	
	
	 @Bean
	 @Order (value = 1000)
    public ServletRegistrationBean servletRegistrationBean() {
        ServletRegistrationBean registration = new ServletRegistrationBean(new CamelHttpTransportServlet(), true,CAMEL_URL_MAPPING);
        registration.setName(CAMEL_SERVLET_NAME);
        return registration;
    }
    
	  @Bean
	  @Order(value = 500)
	    public ServletRegistrationBean dispatcherRegistration(DispatcherServlet dispatcherServlet) {
	        ServletRegistrationBean registration = new ServletRegistrationBean(
	                dispatcherServlet);
	        
	        registration.addUrlMappings("/app/*");
	        return registration;
	    }
    
    @Bean
    public LocalValidatorFactoryBean validator(){
    	return new LocalValidatorFactoryBean();
    }
    
}
