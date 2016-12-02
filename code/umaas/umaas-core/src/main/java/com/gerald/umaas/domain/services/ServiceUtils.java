package com.gerald.umaas.domain.services;

import java.util.HashMap;
import java.util.Map;

public class ServiceUtils {
	public static void checkTypedMap(Map<String, Object> configuration, Map<String, Class<?>> params) {
		Map<String, Object> clone = new HashMap<>(configuration);
		
		clone.forEach((key, value) -> {
			Class<?> keyType = params.get(key);
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
	

}
