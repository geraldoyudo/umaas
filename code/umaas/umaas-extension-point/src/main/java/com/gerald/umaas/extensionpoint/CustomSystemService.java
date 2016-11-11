package com.gerald.umaas.extensionpoint;

import java.util.Map;
import java.util.Set;

public interface CustomSystemService {
	public String getId();
	public String getName();
	public Set<Method> getMethods();
	public void setEnabled(boolean enabled);
	public boolean isEnabled();
	public Map<String,Class<?>> getConfigurationSpecification();
	public Map<String,Object> getConfiguration();
	public void setConfiguration (Map<String,Object>  configuration);
	public Object execute(String methodName, Map<String,Object>  inputParams);
}
