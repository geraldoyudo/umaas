package com.gerald.umaas.file_limit.service;

import java.util.Arrays;
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
import com.gerald.umaas.extensionpoint.CustomSystemService;
import com.gerald.umaas.extensionpoint.Method;

public class DomainLimitService implements CustomSystemService {
	
	private static final String LIMIT_PARAM = "limit";
	private static final String SET_DOMAIN_LIMIT = "setDomainLimit";
	private static final String DOMAIN_PARAM = "domain";
	private static final String GET_ALL_LIMITS = "getAllLimits";
	private static final String GET_DOMAIN_LIMIT = "getDomainLimit";
	private PluginConfigurationRepository repository;
	private DomainConfigurationRepository domainConfigurationRepository;
	private DomainRepository domainRepository;
	
	public DomainLimitService(PluginConfigurationRepository repository,
			DomainConfigurationRepository fileLimitRepository, DomainRepository domainRepository) {
		this.repository = repository;
		this.domainConfigurationRepository = fileLimitRepository;
		this.domainRepository = domainRepository;
	}
	@Override
	public String getId() {
		return "com.gerald.umaas.file_limit.service";
	}

	@Override
	public String getName() {
		return "UMAAS Domain File Limit";
	}

	@Override
	public Set<Method> getMethods() {
		Method domainLimit = new Method();
		domainLimit.setName(GET_DOMAIN_LIMIT);
		Map<String, Class<?>> input = new HashMap<>();
		input.put(DOMAIN_PARAM, String.class);
		domainLimit.setInput(input);
		domainLimit.setOutput(Map.class);
		Method allLimits = new Method();
		allLimits.setName(GET_ALL_LIMITS);
		allLimits.setOutput(List.class);
		Method setDomainLimit = new Method();
		setDomainLimit.setName(SET_DOMAIN_LIMIT);
		input = new HashMap<>();
		input.put(DOMAIN_PARAM, String.class);
		input.put(LIMIT_PARAM, Long.class);
		setDomainLimit.setInput(input);
		return new HashSet<>(Arrays.asList(domainLimit, allLimits, setDomainLimit));
	}


	@Override
	public Map<String, Class<?>> getConfigurationSpecification() {
		Map<String, Class<?>> input = new HashMap<>();
		return input;
	}
	
	@Override
	public Object execute(String methodName, Map<String, Object> inputParams, Map<String,Object> configuation) {
		switch(methodName){
		case GET_DOMAIN_LIMIT: {
			String domainId = inputParams.get(DOMAIN_PARAM).toString();
			checkDomainId(domainId);
			return getLimitByDomain(domainId);
		}
		case GET_ALL_LIMITS:{
			List<DomainConfiguration> configs= domainConfigurationRepository.findAll();
			Map<String, FileLimit> fileLimitMap = new HashMap<>();
			configs.forEach((config) ->{
				Domain d = config.getDomain();
				String id = "ALL";
				if(d != null){
					id = d.getId();
				}
				fileLimitMap.put(id, (FileLimit)config.get(Properties.FILE_LIMIT));
				});
			return fileLimitMap;
		}
		case SET_DOMAIN_LIMIT: {
			String domainId = inputParams.get(DOMAIN_PARAM).toString();
			checkDomainId(domainId);
			long limit = (Long) inputParams.get(LIMIT_PARAM);
			FileLimit fileLimit = getLimitByDomain(domainId);
			fileLimit.setLimit(limit);
			saveLimitByDomain(domainId, fileLimit);
			return true;
		}
		default:{
			throw new IllegalArgumentException("Method not supporoted");
		}
		}
	}
	public FileLimit getLimitByDomain(String domainId) {
		DomainConfiguration config = domainConfigurationRepository.findByDomainId(domainId);
		FileLimit limit;
		if(config == null || config.getProperties().get(Properties.FILE_LIMIT) == null){
			config = domainConfigurationRepository.findByDomainId(null);
			if(config == null || config.getProperties().get(Properties.FILE_LIMIT) == null){
				limit = new FileLimit();
			}else{
				limit = (FileLimit) config.getProperties().get(DomainConfiguration.Properties.FILE_LIMIT);
			}
		}else{
			limit = (FileLimit) config.getProperties().get(DomainConfiguration.Properties.FILE_LIMIT);
		}
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
	public void saveLimitByDomain(String domainId, FileLimit limit) {
		DomainConfiguration config = domainConfigurationRepository.findByDomainId(domainId);
		if(config == null){
			config = new DomainConfiguration();
			Domain d = domainRepository.findOne(domainId);
			if(d == null) throw new NullPointerException("Domain not found");
			config.setDomain(d);
		}
		config.set(Properties.FILE_LIMIT, limit);
		domainConfigurationRepository.save(config);
	}
	
}
