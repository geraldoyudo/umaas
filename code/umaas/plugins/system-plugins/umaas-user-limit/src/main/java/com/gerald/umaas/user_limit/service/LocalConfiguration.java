package com.gerald.umaas.user_limit.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.gerald.umaas.domain.repositories.DomainConfigurationRepository;
import com.gerald.umaas.domain.repositories.DomainRepository;
import com.gerald.umaas.domain.repositories.PluginConfigurationRepository;
import com.gerald.umaas.domain.repositories.UserRepository;
import com.gerald.umaas.user_limit.web.UserLimitEnforcer;

@Configuration
public class LocalConfiguration {
	@Autowired
	private PluginConfigurationRepository configurationRepository;
	@Autowired
	private DomainConfigurationRepository fileLimitRepository;
	@Autowired
	private DomainRepository domainRepository;
	@Autowired
	private UserRepository userRepository;
	
	@Bean
	public UserLimitService userLimitService(){
		return new UserLimitService(configurationRepository, 
				fileLimitRepository, domainRepository, userRepository);
	}
	
	@Bean
	public UserLimitEnforcer fileLimitController(){
		return new UserLimitEnforcer(userLimitService());
	}

}
