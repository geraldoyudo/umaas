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

import com.gerald.umaas.domain.services.CustomSystemServiceProxy;
import com.gerald.umaas.domain.web.utils.ExecutionInput;
import com.gerald.umaas.extensionpoint.TypeSpec;

@RestController
public class CustomSystemServiceEndpoint {
	@Autowired
	private CustomSystemServiceProxy serviceProxy;
	
	@RequestMapping(path = "/system")
	public Collection<CustomSystemServiceProxy.Service> getServices(){
		return serviceProxy.getServices();
	}
	
	@RequestMapping(path = "/system/{id}/name")
	public Map<String, String> getName(@PathVariable("id") String serviceId){
		String name = serviceProxy.getName(serviceId);
		return value(name);
	}
	
	@RequestMapping(path = "/system/{id}/enabled")
	public Map<String, Boolean> isEnabled(@PathVariable("id") String serviceId ){
		boolean enabled = serviceProxy.isEnabled(serviceId );
		return value(enabled);
	}
	
	@RequestMapping(path = "/system/{id}/enabled", 
			method = RequestMethod.POST)
	public void setEnabled(@PathVariable("id") String serviceId,
			@RequestBody Map<String,Boolean> request){
		if(request.get("enabled") == null){
			throw new IllegalArgumentException("Enabled parameter not set");
		}
		serviceProxy.setEnabled(serviceId, request.get("enabled") );
	}
	
	@RequestMapping(path = "/system/{id}/configure", method = RequestMethod.GET)
	public Collection<Map<String, String>> getConfigurationSpecication(@PathVariable("id") String serviceId){
		return serviceProxy.getConfigurationSpecification(serviceId)
				.stream()
				.map(TypeSpec::typeMap)
				.collect(Collectors.toList());
	}
	
	@RequestMapping(path = {"/system/{id}/properties"}, 
			method = RequestMethod.GET)
	public Map<String, Object> getConfiguration(@PathVariable("id") String serviceId){
		return serviceProxy.getConfiguration(serviceId);
	}

	@RequestMapping(path = {"/system/{id}/properties"}, 
			method = RequestMethod.POST)
	public void setConfiguration(@PathVariable("id") String serviceId,
			@RequestBody Map<String, Object> input){
		serviceProxy.setConfiguration(serviceId, input);
	}
	
	@RequestMapping(path = {"/system/{id}/execute"}, 
			method = RequestMethod.POST)
	public Map<String,Object> execute(@PathVariable("id") String serviceId,
			@RequestBody ExecutionInput input){
		
		return value(serviceProxy.execute(serviceId, 
				input.getMethod(), input.getInput()));
	}
	private <T> Map<String, T> value(T name) {
		Map<String,T> output = new HashMap<>();
		output.put("value", name);
		return output;
	}
	
}
