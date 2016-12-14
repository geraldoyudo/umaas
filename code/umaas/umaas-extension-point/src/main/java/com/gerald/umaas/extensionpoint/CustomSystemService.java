package com.gerald.umaas.extensionpoint;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface CustomSystemService {
	public String getId();
	public String getName();
	public Set<Method> getMethods();
	public Collection<TypeSpec> getConfigurationSpecification();
	public Object execute(String methodName, Map<String,Object>  inputParams, Map<String,Object> configuation);
}
