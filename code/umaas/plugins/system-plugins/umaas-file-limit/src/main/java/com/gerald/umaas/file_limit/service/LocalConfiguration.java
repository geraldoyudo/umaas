package com.gerald.umaas.file_limit.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.gerald.umaas.domain.repositories.DomainConfigurationRepository;
import com.gerald.umaas.domain.repositories.DomainRepository;
import com.gerald.umaas.domain.repositories.PluginConfigurationRepository;
import com.gerald.umaas.file_limit.web.FileLimitController;

@Configuration
public class LocalConfiguration {
	@Autowired
	private PluginConfigurationRepository configurationRepository;
	@Autowired
	private DomainConfigurationRepository fileLimitRepository;
	@Autowired
	private DomainRepository domainRepository;
	
	@Bean
	public DomainLimitService systemService(){
		return new DomainLimitService(configurationRepository, 
				fileLimitRepository, domainRepository);
	}
	
	@Bean
	public FileLimitController fileLimitController(){
		return new FileLimitController(systemService());
	}

}
