package com.gerald.umaas.user_limit.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.gerald.umaas.domain.entities.Domain;
import com.gerald.umaas.domain.entities.DomainConfiguration;
import com.gerald.umaas.domain.entities.DomainConfiguration.Properties;
import com.gerald.umaas.domain.entities.ServiceConfiguration;
import com.gerald.umaas.domain.entities.ServiceConfiguration.PluginType;
import com.gerald.umaas.domain.repositories.DomainConfigurationRepository;
import com.gerald.umaas.domain.repositories.DomainRepository;
import com.gerald.umaas.domain.repositories.PluginConfigurationRepository;
import com.gerald.umaas.domain.repositories.UserRepository;
import com.gerald.umaas.extensionpoint.CustomSystemService;
import com.gerald.umaas.extensionpoint.Method;
import com.gerald.umaas.extensionpoint.TypeSpec;

public class UserLimitService implements CustomSystemService {	
	private static final String LIMIT_PARAM = "limit";
	private static final String SET_DOMAIN_LIMIT = "setDomainLimit";
	private static final String DOMAIN_PARAM = "domain";
	private static final String GET_ALL_LIMITS = "getAllLimits";
	private static final String GET_DOMAIN_LIMIT = "getDomainLimit";
	private PluginConfigurationRepository repository;
	private DomainConfigurationRepository domainConfigurationRepository;
	private DomainRepository domainRepository;
	private UserRepository userRepository;
	
	public UserLimitService(PluginConfigurationRepository repository,
			DomainConfigurationRepository fileLimitRepository, DomainRepository domainRepository, 
			UserRepository userRepository) {
		this.repository = repository;
		this.domainConfigurationRepository = fileLimitRepository;
		this.domainRepository = domainRepository;
		this.userRepository = userRepository;
	}
	@Override
	public String getId() {
		return UserLimitService.class.getName();
	}

	@Override
	public String getName() {
		return "UMAAS Domain User Limit";
	}

	@Override
	public Set<Method> getMethods() {
		Method userLimit = new Method();
		userLimit.setName(GET_DOMAIN_LIMIT);
		TypeSpec spec = new TypeSpec(DOMAIN_PARAM, "Domain ID", String.class);
		userLimit.setInput(Arrays.asList(spec));
		userLimit.setOutput(Map.class);
		
		Method allLimits = new Method();
		allLimits.setName(GET_ALL_LIMITS);
		allLimits.setOutput(List.class);
		
		Method setUserLimit = new Method();
		setUserLimit.setName(SET_DOMAIN_LIMIT);
		setUserLimit.setInput(Arrays.asList(
				new TypeSpec(DOMAIN_PARAM, "Domain ID", String.class),
				new TypeSpec(LIMIT_PARAM, "Limit", Long.class)));
	
		return new HashSet<>(Arrays.asList(userLimit, allLimits, setUserLimit));
	
	}


	@Override
	public Collection<TypeSpec> getConfigurationSpecification() {
		return new ArrayList<>();
	}
	
	@Override
	public Object execute(String methodName, Map<String, Object> inputParams, Map<String,Object> configuation) {
		switch(methodName){
		case GET_DOMAIN_LIMIT: {
			return doGetUserLimit(inputParams);
		}
		case GET_ALL_LIMITS:{
			return doGetAllLimits();
		}
		case SET_DOMAIN_LIMIT: {
			return doSetUserLimit(inputParams);
		}
		default:{
			throw new IllegalArgumentException("Method not supporoted");
		}
		}
	}
	
	private Object doGetUserLimit(Map<String, Object> inputParams) {
		String domainId = inputParams.get(DOMAIN_PARAM).toString();
		checkDomainId(domainId);
		return getLimitByDomain(domainId);
	}
	private Object doGetAllLimits() {
		List<DomainConfiguration> configs= domainConfigurationRepository.findAll();
		Map<String, UserLimit> userLimitMap = new HashMap<>();
		configs.forEach((config) ->{
			Domain d = config.getDomain();
			String id = "ALL";
			if(d != null){
				id = d.getId();
			}
			UserLimit limit = getLimitByDomain(id);
			userLimitMap.put(id, limit);
			});
		return userLimitMap;
	}
	private Object doSetUserLimit(Map<String, Object> inputParams) {
		String domainId = inputParams.get(DOMAIN_PARAM).toString();
		checkDomainId(domainId);
		long limit = (Long) inputParams.get(LIMIT_PARAM);
		UserLimit userLimit = getLimitByDomain(domainId);
		userLimit.setLimit(limit);
		saveLimitByDomain(domainId, userLimit);
		return true;
	}
	public UserLimit getLimitByDomain(String domainId) {
		DomainConfiguration config = domainConfigurationRepository.findByDomainId(domainId);
		UserLimit limit;
		if(config == null || config.getProperties().get(Properties.USER_LIMIT) == null){
			config = domainConfigurationRepository.findByDomainId(null);
			if(config == null || config.getProperties().get(Properties.USER_LIMIT) == null){
				limit = new UserLimit();
			}else{
				limit = (UserLimit) config.getProperties().get(DomainConfiguration.Properties.USER_LIMIT);
			}
		}else{
			limit = (UserLimit) config.getProperties().get(DomainConfiguration.Properties.USER_LIMIT);
		}
		limit.setSize(userRepository.countByDomainId(domainId));
		return limit;
	}
	
	public boolean isEnabled(){
		ServiceConfiguration config = repository.findByPluginIdAndTypeAndDomainId(getId(), PluginType.SYSTEM
				, null);
		if(config == null){
			return false;
		}else{
			return config.isEnabled();
		}
	}
	
	private void checkDomainId(String domainId){
		if(domainId == null){
			throw new NullPointerException("Domain is null");
		}
	}
	public void saveLimitByDomain(String domainId, UserLimit limit) {
		DomainConfiguration config = domainConfigurationRepository.findByDomainId(domainId);
		if(config == null){
			config = new DomainConfiguration();
			Domain d = domainRepository.findOne(domainId);
			if(d == null) throw new NullPointerException("Domain not found");
			config.setDomain(d);
		}
		config.set(Properties.USER_LIMIT, limit);
		domainConfigurationRepository.save(config);
	}
	
}
