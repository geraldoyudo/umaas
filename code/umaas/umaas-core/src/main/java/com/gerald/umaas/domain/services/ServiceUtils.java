package com.gerald.umaas.domain.services;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.gerald.umaas.extensionpoint.TypeSpec;

public class ServiceUtils {
	public static void checkTypedMap(Map<String, Object> configuration,
			Collection<TypeSpec> params) {
		Map<String, Object> clone = new HashMap<>(configuration);
		
		clone.forEach((key, value) -> {
			Class<?> keyType = getClassValue(key, params);
			if(keyType == null){
				configuration.remove(key);
				return;
			}
			if(Number.class.isAssignableFrom(keyType)){
				Class<?> valType = value.getClass();
				if(valType.isPrimitive() && !valType.equals(Character.class) || !valType.equals(Void.class)){
					if(keyType.equals(Long.class)){
						value = ((Number) value).longValue();
					}else if(keyType.equals(Double.class)){
						value = ((Number) value).doubleValue();
					}else if(keyType.equals(Float.class)){
						value = ((Number) value).floatValue();
					}else if(keyType.equals(Integer.class)){
						value = ((Number) value).intValue();
					}else if(keyType.equals(Short.class)){
						value = ((Number) value).shortValue();
					}
					configuration.put(key, value);
					return;
				}
			}
			if(!keyType.isInstance(value)){
				throw new IllegalArgumentException("Invalid parameter type in configuration");
			}
		});
	}

	private static Class<?> getClassValue(String key, Collection<TypeSpec> specs){
		for(TypeSpec spec: specs){
			try{
			if(spec.getKey().equals(key)) 
				return spec.getValueClass();
			}catch(NullPointerException ex){
				continue;
			}
		}
		return null;
	}
}
