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

import com.gerald.umaas.domain.entities.ServiceConfiguration;
import com.gerald.umaas.domain.entities.ServiceConfiguration.PluginType;
import com.gerald.umaas.domain.repositories.PluginConfigurationRepository;
import com.gerald.umaas.extensionpoint.TypeSpec;
import com.gerald.umaas.extensionpoint.CustomSystemService;
import com.gerald.umaas.extensionpoint.Method;

import lombok.AllArgsConstructor;
import lombok.Data;

@Component
public class CustomSystemServiceProxy{
	private Map<String,CustomSystemService> serviceMap = new TreeMap<>();
	@Autowired
	private PluginConfigurationRepository pluginConfigurationRepository;
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
		checkServiceId(serviceId);
		ServiceConfiguration config = pluginConfigurationRepository.findByPluginIdAndTypeAndDomainId(serviceId, 
				PluginType.SYSTEM, null);
		if(config == null){
			config = new ServiceConfiguration(serviceId, PluginType.SYSTEM, 
					null, enabled, new HashMap<>());
			System.out.println(config);
		}else{
			config.setEnabled(enabled);
		}
		pluginConfigurationRepository.save(config);
	}

	public boolean isEnabled(String serviceId ){
		checkServiceId(serviceId);
		ServiceConfiguration config = pluginConfigurationRepository.findByPluginIdAndTypeAndDomainId(serviceId, PluginType.SYSTEM, null);
		if(config == null){
			return false;
		}else{
			return config.isEnabled();
		}
	}
	
	private void checkServiceId(String serviceId) {
		CustomSystemService service = getService(serviceId);
		if(service == null){
			throw new NullPointerException("Service is null!!");
		}
		
		
	}
	public Collection<TypeSpec> getConfigurationSpecification(String serviceId){
		return getService(serviceId).getConfigurationSpecification();
	}
	
	public void setConfiguration(String serviceId, Map<String,Object> configuration){
		
		checkServiceId(serviceId);
		if(configuration == null) throw new NullPointerException("Null Configuration");
		CustomSystemService service = getService(serviceId);
		
		Collection<TypeSpec> params = service.getConfigurationSpecification();
		ServiceUtils.checkTypedMap(configuration, params);
		ServiceConfiguration config = pluginConfigurationRepository.findByPluginIdAndTypeAndDomainId(serviceId, PluginType.SYSTEM, null);
		if(config == null){
			config = new ServiceConfiguration(serviceId, PluginType.SYSTEM, 
					null, false, configuration);
		}else{
			config.setConfiguration(configuration);
		}
		pluginConfigurationRepository.save(config);
	}

	public Map<String,Object> getConfiguration(String serviceId ){
		checkServiceId(serviceId);
		ServiceConfiguration config = pluginConfigurationRepository.findByPluginIdAndTypeAndDomainId(serviceId, PluginType.SYSTEM, null);
		if(config == null){
			return new HashMap<>();
		}else{
			return config.getConfiguration();
		}
	}
	
	public Object execute(String serviceId, String method, 
			Map<String,Object> inputParams){
		
		if(method == null) throw new NullPointerException("Method name is null");
		CustomSystemService service = getService(serviceId);
		ServiceConfiguration config = pluginConfigurationRepository.findByPluginIdAndTypeAndDomainId(serviceId,
				PluginType.SYSTEM, null);
		if(config == null || !config.isEnabled()){
			throw new IllegalStateException("Service is disabled");
		}
		for(Method m: service.getMethods()){
			if(m.getName().equals(method)){
				ServiceUtils.checkTypedMap(inputParams, m.getInput());
				return service.execute( method, inputParams, config.getConfiguration());
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
	
	
	@Data
	@AllArgsConstructor
	public static class Service{
		private String name;
		private String id;
	}
}
