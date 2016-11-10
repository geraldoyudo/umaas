package com.gerald.umaas.extensionpoint;

import java.util.Map;
import java.util.Set;

public interface CustomDomainService {
	public String getId();
	public String getName();
	public Set<Method> getMethods();
	public void setEnabled(String domainId, boolean enabled);
	public boolean isEnabled(String domainId);
	public Map<String,Class<?>> getConfigurationSpecification();
	public Map<String,Object> getConfiguration(String domainId);
	public void setConfiguration (String domainId, Map<String,Object>  configuration);
	public Object execute(String domainId, String methodName, Map<String,Object>  inputParams);
}
