package com.gerald.umaas.domain.services;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gerald.umaas.domain.entities.Domain;
import com.gerald.umaas.domain.entities.ServiceConfiguration;
import com.gerald.umaas.domain.entities.ServiceConfiguration.PluginType;
import com.gerald.umaas.domain.repositories.DomainRepository;
import com.gerald.umaas.domain.repositories.PluginConfigurationRepository;
import com.gerald.umaas.extensionpoint.CustomDomainService;
import com.gerald.umaas.extensionpoint.Method;
import com.gerald.umaas.extensionpoint.TypeSpec;

import lombok.AllArgsConstructor;
import lombok.Data;

@Component
public class CustomDomainServiceProxy{
	private static final String DEFAULT = "default";
	private Map<String,CustomDomainService> domainServiceMap = new TreeMap<>();
	@Autowired
	private PluginConfigurationRepository pluginConfigurationRepository;
	@Autowired
	private DomainRepository domainRepository;
	
	@Autowired(required = false)
	public CustomDomainServiceProxy(List<CustomDomainService> services) {
		services.forEach((service) -> {
			domainServiceMap.put(service.getId(), service);
		});
	}
	
	public CustomDomainServiceProxy() {
	}
	public String getName(String serviceId){
		return getService(serviceId).getName();
	}
	public Set<Method> getMethods(String serviceId){
		return  getService(serviceId).getMethods();
	}
	
	public void setEnabled(String serviceId, String domainId, boolean enabled){
		ServiceConfiguration config = null;
		checkServiceId(serviceId);
		if(!domainId.equals( DEFAULT)){
			Domain d = checkDomainId(domainId);
			 config = pluginConfigurationRepository.findByPluginIdAndTypeAndDomainId(serviceId, PluginType.DOMAIN, domainId);
			 if(config == null){
					config = new ServiceConfiguration(serviceId, PluginType.DOMAIN, 
							d, enabled, new HashMap<>());
			}else{
				config.setEnabled(enabled);
			}
		}else{
			config = pluginConfigurationRepository.findByPluginIdAndTypeAndDomainIsNull(serviceId, PluginType.DOMAIN);
			if(config == null){
				config = new ServiceConfiguration(serviceId, PluginType.DOMAIN, 
						null, enabled, new HashMap<>());
			}else{
				config.setEnabled(enabled);
			}
		}
		pluginConfigurationRepository.save(config);
	}

	private void checkServiceId(String serviceId) {
		CustomDomainService service = getService(serviceId);
		if(service == null){
			throw new NullPointerException("Service is null!!");
		}
		
	}

	public boolean isEnabled(String serviceId, String domainId){
		ServiceConfiguration config;
		checkServiceId(serviceId);
		if(!domainId.equals( DEFAULT)){
			checkDomainId(domainId);
			 config = pluginConfigurationRepository
					.findByPluginIdAndTypeAndDomainId(serviceId, PluginType.DOMAIN,
							domainId);
			if(config != null){
				return config.isEnabled();
			}
		}
		config = pluginConfigurationRepository
				.findByPluginIdAndTypeAndDomainIsNull(serviceId, PluginType.DOMAIN);
		if(config != null){
			return config.isEnabled();
		}
		System.out.println(String.format("Config is null for %s and %s", serviceId, domainId));
		return true;
	}
	
	public Collection<TypeSpec> getConfigurationSpecification(String serviceId){
		return getService(serviceId).getConfigurationSpecification();
	}
	
	public void setConfiguration(String serviceId, String domainId, Map<String,Object> configuration){
		if(configuration == null) throw new NullPointerException("Null Configuration");
		ServiceConfiguration config = null;
		checkServiceId(serviceId);
		CustomDomainService service = getService(serviceId);
		Collection<TypeSpec> params = service.getConfigurationSpecification();
		ServiceUtils.checkTypedMap(configuration, params);
		if(!domainId.equals( DEFAULT)){
			Domain d = checkDomainId(domainId);
			 config = pluginConfigurationRepository.findByPluginIdAndTypeAndDomainId(serviceId, PluginType.DOMAIN, domainId);
			 if(config == null){
					config = new ServiceConfiguration(serviceId, PluginType.DOMAIN, 
							d, false, configuration);
			}
		}else{
			config = pluginConfigurationRepository.findByPluginIdAndTypeAndDomainIsNull(serviceId, PluginType.DOMAIN);
			if(config == null){
				config = new ServiceConfiguration(serviceId, PluginType.DOMAIN, 
						null, false, configuration);
			}else{
				config.setConfiguration(configuration);
			}
		}
		
		pluginConfigurationRepository.save(config);
	}

	public Map<String,Object> getConfiguration(String serviceId, String domainId){
		ServiceConfiguration config;
		checkServiceId(serviceId);
		if(!domainId.equals( DEFAULT)){
			checkDomainId(domainId);
			 config = pluginConfigurationRepository
					.findByPluginIdAndTypeAndDomainId(serviceId, PluginType.DOMAIN,
							domainId);
			if(config != null){
				return config.getConfiguration();
			}
		}
		config = pluginConfigurationRepository
				.findByPluginIdAndTypeAndDomainIsNull(serviceId, PluginType.DOMAIN);
		if(config != null){
			return config.getConfiguration();
		}
		System.out.println(String.format("Config is null for %s and %s", serviceId, domainId));
		return new HashMap<>();
	}
	
	public Object execute(String serviceId, String domainId, String method, 
			Map<String,Object> inputParams){
		checkDomainId(domainId);
		if(method == null) throw new NullPointerException("Method name is null");
		CustomDomainService service = getService(serviceId);
		ServiceConfiguration config = pluginConfigurationRepository
				.findByPluginIdAndTypeAndDomainId(serviceId, PluginType.DOMAIN,
						domainId);
		if(config == null || !config.isEnabled()){
			throw new IllegalStateException("Service is disabled for this domain");
		}
		for(Method m: service.getMethods()){
			if(m.getName().equals(method)){
				ServiceUtils.checkTypedMap(inputParams, m.getInput());
				return service.execute(domainId, method, inputParams, config.getConfiguration());
			}
		}
		throw new IllegalArgumentException("Method not available for this service");
	}
	
	public Collection<Service> getServices(){
		return domainServiceMap.values().stream().map((service) -> {
			return new Service(service.getName(), service.getId());
		}).collect(Collectors.toList());
	}
	private CustomDomainService getService(String serviceId) {
		if(serviceId == null) throw new NullPointerException("Null service Id");
		CustomDomainService service = domainServiceMap.get(serviceId);
		if(service == null) throw new NullPointerException("No service available with the specified id."
				+ " Check that you installed the plugin successfully.");
		return service;
	}
	private Domain checkDomainId(String domainId) {
		if(domainId == null) throw new NullPointerException("Domain ID not set");
		Domain d = domainRepository.findOne(domainId);
		if(d == null)  throw new NullPointerException("No domain with the specified ID");
		return d;
	}
	
	@Data
	@AllArgsConstructor
	public static class Service{
		private String name;
		private String id;
	}
}
