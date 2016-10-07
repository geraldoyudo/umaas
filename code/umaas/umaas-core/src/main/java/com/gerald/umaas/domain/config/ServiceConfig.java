package com.gerald.umaas.domain.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
}
