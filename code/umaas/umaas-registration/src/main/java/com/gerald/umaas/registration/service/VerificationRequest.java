package com.gerald.umaas.registration.service;

import java.util.Map;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class VerificationRequest {
	@NotNull
	private String name;
	@NotNull
	private String value;
	private Map<String,Object> properties;
	
	public Object get(String key){
		return properties.get(key);
	}
	public void set(String key, Object val){
		properties.put(key, val);
	}
}
