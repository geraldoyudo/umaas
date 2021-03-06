package com.gerald.umaas.domain.config;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import org.springframework.web.client.RestTemplate;

import com.gerald.utils.general.CodeGenerator;
import com.gerald.utils.general.RandomCodeStringGenerator;

@Configuration
public class ServiceConfig {

	 @Bean
    @Qualifier("domainCode")
    public CodeGenerator<String> domainCodeGenerator(){
        RandomCodeStringGenerator gen = new RandomCodeStringGenerator();
        gen.setCharLength(16);
        return gen;
    }
	 @Bean
	 public RestTemplate restTemplate(){
		 return new RestTemplate();
	 }
	 @Bean
	 public VelocityEngine velocityEngine(){
		 return new VelocityEngine();
	 }
	 @Bean // Serialize message content to json using TextMessage
	    public MessageConverter jacksonJmsMessageConverter() {
	        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
	        converter.setTargetType(MessageType.TEXT);
	        converter.setTypeIdPropertyName("_type");
	        return converter;
	    }
}
