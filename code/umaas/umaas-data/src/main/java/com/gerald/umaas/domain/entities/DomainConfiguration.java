package com.gerald.umaas.domain.entities;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@Document
@CompoundIndexes({
    @CompoundIndex(name = "domain", def = "{'domain.$id' : 1}", unique = true)
})
@Data
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
public class DomainConfiguration extends DomainResource {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2100162578774320630L;
	private Map<String, Object> properties = new HashMap<>();
	
	public Object get(String property){
		return properties.get(property);
	}
	public Object set(String property, Object value){
		return properties.put(property, value);
	}
	
	public static class Properties {
		public static final String FILE_LIMIT = "fileLImit";
		public static final String USER_LIMIT = "userLimit";
	}
}
