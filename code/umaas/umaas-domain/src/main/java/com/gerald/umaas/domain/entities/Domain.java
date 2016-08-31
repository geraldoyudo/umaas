package com.gerald.umaas.domain.entities;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper= true)
@Document
public class Domain extends Resource{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2317536290238667335L;
	@Indexed(unique = true)
	private String name;
	@Indexed(unique = true)
	private String code;
	private Map<String, Object> properties = new HashMap<>();
}
