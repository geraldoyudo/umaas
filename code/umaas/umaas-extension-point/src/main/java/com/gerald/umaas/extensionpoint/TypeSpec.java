package com.gerald.umaas.extensionpoint;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(of = "key")
@NoArgsConstructor
@AllArgsConstructor
public class TypeSpec {
	private String key;
	private String name;
	private Class<?> valueClass;
	private Map<String,String> properties = new HashMap<>();
	
	public TypeSpec(String key, String name, Class<?> clazz){
		this.key = key;
		this.name = name;
		this.valueClass = clazz;
	}
	public static Map<String,String> typeMap(TypeSpec spec){
		Map<String,String> map = new HashMap<>();
		map.put("key", spec.getKey());
		map.put("name", spec.getName());
		map.put("type", spec.getValueClass().getSimpleName());
		map.putAll(spec.getProperties());
		return map;
	}
}
