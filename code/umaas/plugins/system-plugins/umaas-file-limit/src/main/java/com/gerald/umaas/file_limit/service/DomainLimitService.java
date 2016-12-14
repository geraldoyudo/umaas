package com.gerald.umaas.file_limit.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

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
import com.gerald.umaas.extensionpoint.TypeSpec;

public class DomainLimitService implements CustomSystemService {
	
	private static final String DOMAIN_DIRECTORY_FORMAT = "umaas/domain/%s";
	private static final String CALCULATE_ALL_DOMAIN_SIZES = "calculateAllDomainSizes";
	private static final String CALCULATE_DOMAIN_SIZE = "calculateDomainSize";
	private static final String LIMIT_PARAM = "limit";
	private static final String SET_DOMAIN_LIMIT = "setDomainLimit";
	private static final String DOMAIN_PARAM = "domain";
	private static final String GET_ALL_LIMITS = "getAllLimits";
	private static final String GET_DOMAIN_LIMIT = "getDomainLimit";
	private PluginConfigurationRepository repository;
	private DomainConfigurationRepository domainConfigurationRepository;
	private DomainRepository domainRepository;
	@Value("${umaas.file-repo.url}")
	private String fileRepoUrl;
	@Autowired
	private RestTemplate restTemplate;
	
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
		TypeSpec spec = new TypeSpec(DOMAIN_PARAM, "Domain ID", String.class);
		domainLimit.setInput(Arrays.asList(spec));
		domainLimit.setOutput(Map.class);
		
		Method allLimits = new Method();
		allLimits.setName(GET_ALL_LIMITS);
		allLimits.setOutput(List.class);
		
		Method setDomainLimit = new Method();
		setDomainLimit.setName(SET_DOMAIN_LIMIT);
		setDomainLimit.setInput(Arrays.asList(
				new TypeSpec(DOMAIN_PARAM, "Domain ID", String.class),
				new TypeSpec(LIMIT_PARAM, "Limit", Long.class)));
		
		Method calculateDomainSize = new Method();
		calculateDomainSize.setName(CALCULATE_DOMAIN_SIZE);
		calculateDomainSize.setInput(Arrays.asList(
				new TypeSpec(DOMAIN_PARAM, "Domain ID", String.class)
			));
		calculateDomainSize.setOutput(Long.class);
		
		Method calculateAllDomainSizes = new Method();
		calculateAllDomainSizes.setName(CALCULATE_ALL_DOMAIN_SIZES);
		calculateAllDomainSizes.setOutput(Boolean.class);
		return new HashSet<>(Arrays.asList(domainLimit, allLimits, setDomainLimit, 
				calculateDomainSize, calculateAllDomainSizes));
	
	}


	@Override
	public Collection<TypeSpec> getConfigurationSpecification() {
		return new ArrayList<>();
	}
	
	@Override
	public Object execute(String methodName, Map<String, Object> inputParams, Map<String,Object> configuation) {
		switch(methodName){
		case GET_DOMAIN_LIMIT: {
			return doGetDomainLimit(inputParams);
		}
		case GET_ALL_LIMITS:{
			return doGetAllLimits();
		}
		case SET_DOMAIN_LIMIT: {
			return doSetDomainLimit(inputParams);
		}
		case CALCULATE_DOMAIN_SIZE:{
			return doCalculateDomainSize(inputParams);
		}
		case CALCULATE_ALL_DOMAIN_SIZES:{
			return doCalculateAllDomainSizes(inputParams);
		}
		default:{
			throw new IllegalArgumentException("Method not supporoted");
		}
		}
	}
	
	private Object doCalculateAllDomainSizes(Map<String, Object> inputParams) {
		calculateDomainSizesAsync();
		return true;
	}
	
	@Async
	private void calculateDomainSizesAsync(){
		for(Domain d: domainRepository.findAll()){
			calculateDomainSize(d.getId());
		}
	}
	private Object doCalculateDomainSize(Map<String, Object> inputParams) {
		String domainId = inputParams.get(DOMAIN_PARAM).toString();
		return calculateDomainSize(domainId);
	}
	private long calculateDomainSize(String domainId){
		checkDomainId(domainId);
		String query = UriComponentsBuilder.fromHttpUrl(fileRepoUrl + "/files/size").queryParam("directory" ,
				String.format(DOMAIN_DIRECTORY_FORMAT, domainId)).toUriString();
		System.out.println(query);
		Map<String,Object> ret =  restTemplate.getForObject(query, HashMap.class);
		System.out.println(ret);
		long size =((Number)ret.get("size")).longValue();
		FileLimit limit = getLimitByDomain(domainId);
		limit.setSize(size);
		saveLimitByDomain(domainId, limit);
		return size;
	}
	private Object doGetDomainLimit(Map<String, Object> inputParams) {
		String domainId = inputParams.get(DOMAIN_PARAM).toString();
		checkDomainId(domainId);
		return getLimitByDomain(domainId);
	}
	private Object doGetAllLimits() {
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
	private Object doSetDomainLimit(Map<String, Object> inputParams) {
		String domainId = inputParams.get(DOMAIN_PARAM).toString();
		checkDomainId(domainId);
		long limit = (Long) inputParams.get(LIMIT_PARAM);
		FileLimit fileLimit = getLimitByDomain(domainId);
		fileLimit.setLimit(limit);
		saveLimitByDomain(domainId, fileLimit);
		return true;
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
