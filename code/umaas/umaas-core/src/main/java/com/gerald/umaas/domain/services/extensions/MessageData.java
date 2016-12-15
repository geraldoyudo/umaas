package com.gerald.umaas.domain.services.extensions;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;

@Data
public class MessageData {
	private String[] to;
	private String from;
	private String body;
	private MessageType type = MessageType.EMAIL;
	private Map<String,Object> properties = new HashMap<>();
	public void set(String key, Object value){
		properties.put(key, value);
	}
	public Object get(String key){
		return properties.get(key);
	}
	public Object get(String key, Object defaultValue){
		Object val = properties.get(key);
		if(val == null)
			val = defaultValue;
		return val;
	}
	public enum MessageType {EMAIL, SMS}
}
