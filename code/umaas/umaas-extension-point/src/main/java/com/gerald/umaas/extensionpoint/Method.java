package com.gerald.umaas.extensionpoint;

import java.util.Collection;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Method {
	private String name;
	private Collection<TypeSpec> input;
	private Class<?> output;
}
