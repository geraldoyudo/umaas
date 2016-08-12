package com.gerald.umaas.domain.utils;

import java.util.HashMap;
import java.util.Map;

public class MapBuilder<T,V> {
	private HashMap<T,V> map = new HashMap<>();
	
	public MapBuilder<T,V> put(T key, V value){
		map.put(key, value);
		return this;
	}
	public Map<T,V> build(){
		return map;
	}
}
