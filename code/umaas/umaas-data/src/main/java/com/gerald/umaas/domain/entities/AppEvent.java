package com.gerald.umaas.domain.entities;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Document
@Data
@EqualsAndHashCode(callSuper = true)
public class AppEvent extends DomainResource{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8097355406393241030L;
	
	private Date date = new Date();
	private Map<String,Object> data = new HashMap<>();
	private String type = "default";
	public Object get(String key){
		return data.get(key);
	}
	public Object get(String key, Object defaultValue){
		return data.getOrDefault(key, defaultValue);
	}
	
	public void set(String key, Object value){
		data.put(key, value);
	}
}
