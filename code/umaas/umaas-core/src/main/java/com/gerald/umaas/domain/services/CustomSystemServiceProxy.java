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

import com.gerald.umaas.extensionpoint.CustomSystemService;
import com.gerald.umaas.extensionpoint.Method;

import lombok.AllArgsConstructor;
import lombok.Data;

@Component
public class CustomSystemServiceProxy{
	private Map<String,CustomSystemService> serviceMap = new TreeMap<>();
	
	@Autowired(required = false)
	public CustomSystemServiceProxy(List<CustomSystemService> services) {
		services.forEach((service) -> {
			serviceMap.put(service.getId(), service);
		});
	}
	public CustomSystemServiceProxy() {
		// TODO Auto-generated constructor stub
	}
	public String getName(String serviceId){
		return getService(serviceId).getName();
	}
	public Set<Method> getMethods(String serviceId){
		return  getService(serviceId).getMethods();
	}
	
	public void setEnabled(String serviceId, boolean enabled){
		
		getService(serviceId).setEnabled( enabled);
	}

	public boolean isEnabled(String serviceId ){
		return getService(serviceId).isEnabled();
	}
	
	public Map<String,Class<?>> getConfigurationSpecification(String serviceId){
		return getService(serviceId).getConfigurationSpecification();
	}
	
	public void setConfiguration(String serviceId, Map<String,Object> configuration){
		
		if(configuration == null) throw new NullPointerException("Null Configuration");
		CustomSystemService service = getService(serviceId);
		
		Map<String,Class<?>> params = service.getConfigurationSpecification();
		checkTypedMap(configuration, params);
		
		service.setConfiguration( configuration);	
	}

	public Map<String,Object> getConfiguration(String serviceId ){
		return getService(serviceId).getConfiguration();
	}
	
	public Object execute(String serviceId, String method, 
			Map<String,Object> inputParams){
		
		if(method == null) throw new NullPointerException("Method name is null");
		CustomSystemService service = getService(serviceId);
		for(Method m: service.getMethods()){
			if(m.getName().equals(method)){
				checkTypedMap(inputParams, m.getInput());
				return service.execute( method, inputParams);
			}
		}
		throw new IllegalArgumentException("Method not available for this service");
	}
	
	public Collection<Service> getServices(){
		return serviceMap.values().stream().map((service) -> {
			return new Service(service.getName(), service.getId());
		}).collect(Collectors.toList());
	}
	private CustomSystemService getService(String serviceId) {
		if(serviceId == null) throw new NullPointerException("Null service Id");
		CustomSystemService service = serviceMap.get(serviceId);
		if(service == null) throw new NullPointerException("No service available with the specified id."
				+ " Check that you installed the plugin successfully.");
		return service;
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
