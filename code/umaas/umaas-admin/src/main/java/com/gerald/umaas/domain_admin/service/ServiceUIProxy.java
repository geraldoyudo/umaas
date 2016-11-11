package com.gerald.umaas.domain_admin.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gerald.umaas.extensionpoint.ServiceUI;

import lombok.AllArgsConstructor;
import lombok.Data;

@Component
public class ServiceUIProxy{
	private Map<String,ServiceUI> serviceMap = new TreeMap<>();
	
	@Autowired(required = false)
	public ServiceUIProxy(List<ServiceUI> services) {
		services.forEach((service) -> {
			serviceMap.put(service.getId(), service);
		});
	}
	public ServiceUIProxy() {
		// TODO Auto-generated constructor stub
	}
	public String getName(String serviceId){
		return getService(serviceId).getName();
	}
	public String getDescription(String serviceId){
		return  getService(serviceId).getDescription();
	}
	
	public String getTemplate(String serviceId){
		return  getService(serviceId).getTemplate();
	}
	
	public void setEnabled(String serviceId,String domainId, boolean enabled){
		
		getService(serviceId).setEnabled( domainId, enabled);
	}

	public boolean isEnabled(String serviceId, String domainId){
		return getService(serviceId).isEnabled(domainId);
	}
	
	public Collection<Service> getServices(){
		return serviceMap.values().stream().map((service) -> {
			return new Service(service.getName(), service.getId());
		}).collect(Collectors.toList());
	}
	private ServiceUI getService(String serviceId) {
		if(serviceId == null) throw new NullPointerException("Null service Id");
		ServiceUI service = serviceMap.get(serviceId);
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
