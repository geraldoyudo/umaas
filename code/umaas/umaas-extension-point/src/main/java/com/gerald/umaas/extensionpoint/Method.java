package com.gerald.umaas.extensionpoint;

import java.util.Collection;

import lombok.Data;

@Data
public class Method {
	private String name;
	private Collection<TypeSpec> input;
	private Class<?> output;
}
