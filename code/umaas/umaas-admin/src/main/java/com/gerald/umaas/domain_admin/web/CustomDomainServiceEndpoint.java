package com.gerald.umaas.domain_admin.web;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.gerald.umaas.domain_admin.service.ServiceUIProxy;
import com.gerald.umaas.domain_admin.service.ServiceUIProxy.Service;

@RestController
public class CustomDomainServiceEndpoint {
	@Autowired
	private ServiceUIProxy serviceProxy;
	
	@RequestMapping(path = "/serviceUI")
	public Collection<ServiceUIProxy.Service> getServices(){
		return serviceProxy.getServices();
	}
	
	@RequestMapping(path = "/serviceUI/{id}/name")
	public Map<String, String> getName(@PathVariable("id") String serviceId){
		String name = serviceProxy.getName(serviceId);
		return value(name);
	}
	
	@RequestMapping(path = "/serviceUI/{id}/description")
	public Map<String, String> getDescription(@PathVariable("id") String serviceId){
		String name = serviceProxy.getDescription(serviceId);
		return value(name);
	}
	
	@RequestMapping(path = "/serviceUI/{id}/template", produces = "text/html")
	public String getTemplate(@PathVariable("id") String serviceId){
		String name = serviceProxy.getTemplate(serviceId);
		return name;
	}
	
	@RequestMapping(path = "/serviceUI/{id}/{domainId}/enabled")
	public Map<String, Boolean> isEnabled(@PathVariable("id") String serviceId, 
			@PathVariable("domainId") String domainId){
		boolean enabled = serviceProxy.isEnabled(serviceId,domainId );
		return value(enabled);
	}
	
	@RequestMapping(path = "/serviceUI/{id}/{domainId}/enabled", 
			method = RequestMethod.POST)
	public void setEnabled(@PathVariable("id") String serviceId, 
			@PathVariable("domainId") String domainId, 
			@RequestBody Map<String,Boolean> request){
		if(request.get("enabled") == null){
			throw new IllegalArgumentException("Enabled parameter not set");
		}
		serviceProxy.setEnabled(serviceId,domainId, request.get("enabled") );
	}
	
	private <T> Map<String, T> value(T name) {
		Map<String,T> output = new HashMap<>();
		output.put("value", name);
		return output;
	}
	
	
}
