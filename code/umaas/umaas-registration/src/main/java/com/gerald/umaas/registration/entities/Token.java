package com.gerald.umaas.registration.entities;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Document
@AllArgsConstructor
@RequiredArgsConstructor
public class Token {
	@Id
	private String id;
	@NotNull
	private String code;
    private Date expiryDate;
	@NotNull
  	private String entityType="";
	@NotNull
    private String purpose;
	private Map<String,Object> properties = new HashMap<>();
	public Object get(String key){
		return properties.get(key);
	}
	public void set(String key, String value){
		properties.put(key,value);
	}
}
