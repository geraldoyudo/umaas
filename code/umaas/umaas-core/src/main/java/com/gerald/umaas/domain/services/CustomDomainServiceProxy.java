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

import com.gerald.umaas.extensionpoint.CustomDomainService;
import com.gerald.umaas.extensionpoint.Method;

import lombok.AllArgsConstructor;
import lombok.Data;

@Component
public class CustomDomainServiceProxy{
	private Map<String,CustomDomainService> domainServiceMap = new TreeMap<>();
	
	@Autowired
	public CustomDomainServiceProxy(List<CustomDomainService> services) {
		services.forEach((service) -> {
			domainServiceMap.put(service.getId(), service);
		});
	}
	public String getName(String serviceId){
		return getService(serviceId).getName();
	}
	public Set<Method> getMethods(String serviceId){
		return  getService(serviceId).getMethods();
	}
	
	public void setEnabled(String serviceId, String domainId, boolean enabled){
		checkDomainId(domainId);
		getService(serviceId).setEnabled(domainId, enabled);
	}

	public boolean isEnabled(String serviceId, String domainId){
		checkDomainId(domainId);
		return getService(serviceId).isEnabled(domainId);
	}
	
	public Map<String,Class<?>> getConfigurationSpecification(String serviceId){
		return getService(serviceId).getConfigurationSpecification();
	}
	
	public void setConfiguration(String serviceId, String domainId, Map<String,Object> configuration){
		checkDomainId(domainId);
		if(configuration == null) throw new NullPointerException("Null Configuration");
		CustomDomainService service = getService(serviceId);
		
		Map<String,Class<?>> params = service.getConfigurationSpecification();
		checkTypedMap(configuration, params);
		
		service.setConfiguration(domainId, configuration);	
	}

	public Map<String,Object> getConfiguration(String serviceId, String domainId){
		checkDomainId(domainId);
		return getService(serviceId).getConfiguration(domainId);
	}
	
	public Object execute(String serviceId, String domainId, String method, 
			Map<String,Object> inputParams){
		checkDomainId(domainId);
		if(method == null) throw new NullPointerException("Method name is null");
		CustomDomainService service = getService(serviceId);
		for(Method m: service.getMethods()){
			if(m.getName().equals(method)){
				checkTypedMap(inputParams, m.getInput());
				return service.execute(domainId, method, inputParams);
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
	private void checkDomainId(String domainId) {
		if(domainId == null) throw new NullPointerException("Domain ID not set");
		
	}
	
	private void checkTypedMap(Map<String, Object> configuration, Map<String, Class<?>> params) {
		Map<String, Object> clone = new HashMap<>(configuration);
		
		clone.forEach((key, value) -> {
			Class<?> keyType = params.get(key);
			if(keyType == null){
				configuration.remove(key);
				return;
			}
			if(!keyType.isInstance(value)){
				throw new IllegalArgumentException("Invalid parameter type in configuration");
			}
		});
	}
	
	@Data
	@AllArgsConstructor
	public static class Service{
		private String name;
		private String id;
	}
}
