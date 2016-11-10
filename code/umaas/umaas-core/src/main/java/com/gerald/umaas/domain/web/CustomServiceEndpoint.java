package com.gerald.umaas.domain.web;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.gerald.umaas.domain.services.CustomDomainServiceProxy;

import lombok.Data;

@RestController
public class CustomServiceEndpoint {
	@Autowired
	private CustomDomainServiceProxy serviceProxy;
	
	@RequestMapping(path = "/endpoint")
	public Collection<CustomDomainServiceProxy.Service> getServices(){
		return serviceProxy.getServices();
	}
	
	@RequestMapping(path = "/endpoint/{id}/name")
	public Map<String, String> getName(@PathVariable("id") String serviceId){
		String name = serviceProxy.getName(serviceId);
		return value(name);
	}
	
	@RequestMapping(path = "/endpoint/{id}/{domainId}/enabled")
	public Map<String, Boolean> isEnabled(@PathVariable("id") String serviceId, 
			@PathVariable("domainId") String domainId){
		boolean enabled = serviceProxy.isEnabled(serviceId,domainId );
		return value(enabled);
	}
	
	@RequestMapping(path = "/endpoint/{id}/{domainId}/enabled", 
			method = RequestMethod.POST)
	public void setEnabled(@PathVariable("id") String serviceId, 
			@PathVariable("domainId") String domainId, 
			@RequestBody Map<String,Boolean> request){
		if(request.get("enabled") == null){
			throw new IllegalArgumentException("Enabled parameter not set");
		}
		serviceProxy.setEnabled(serviceId,domainId, request.get("enabled") );
	}
	
	@RequestMapping(path = {"/endpoint/{id}/configure"}, 
			method = RequestMethod.GET)
	public Map<String, String> getConfigurationSpecication(@PathVariable("id") String serviceId){
		
		return serviceProxy.getConfigurationSpecification(serviceId)
		.entrySet()
		.stream()
		.collect((Collectors.toMap(Map.Entry<String,Class<?>>::getKey ,
				(entry) -> {return entry.getValue().getSimpleName();}, 
				(a,b) -> a, TreeMap::new)));
	}
	
	@RequestMapping(path = {"/endpoint/{id}/{domainId}/properties"}, 
			method = RequestMethod.GET)
	public Map<String, Object> getConfiguration(@PathVariable("id") String serviceId,
			@PathVariable("domainId") String domainId){
		return serviceProxy.getConfiguration(serviceId, domainId);
	}

	@RequestMapping(path = {"/endpoint/{id}/{domainId}/properties"}, 
			method = RequestMethod.POST)
	public void setConfiguration(@PathVariable("id") String serviceId,
			@PathVariable("domainId") String domainId,
			@RequestBody Map<String, Object> input){
		serviceProxy.setConfiguration(serviceId, domainId, input);
	}
	
	@RequestMapping(path = {"/endpoint/{id}/{domainId}/execute"}, 
			method = RequestMethod.POST)
	public Map<String,Object> execute(@PathVariable("id") String serviceId,
			@PathVariable("domainId") String domainId,
			@RequestBody ExecutionInput input){
		
		return value(serviceProxy.execute(serviceId, domainId, 
				input.getMethod(), input.getInput()));
	}
	private <T> Map<String, T> value(T name) {
		Map<String,T> output = new HashMap<>();
		output.put("value", name);
		return output;
	}
	
	
	@Data
	public static class ExecutionInput{
		private String method;
		private Map<String, Object> input;
	}
}
