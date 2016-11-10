package com.gerald.umaas.extensionpoint;

import java.util.Map;

import lombok.Data;

@Data
public class Method {
	private String name;
	private Map<String,Class<?>> input;
	private Class<?> output;
}
