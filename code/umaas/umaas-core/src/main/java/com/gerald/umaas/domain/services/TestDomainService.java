package com.gerald.umaas.domain.services;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.gerald.umaas.extensionpoint.CustomDomainService;
import com.gerald.umaas.extensionpoint.Method;

@Component
public class TestDomainService implements CustomDomainService{
	
	@Override
	public String getId() {
		return "test";
	}

	@Override
	public String getName() {
		return "test";
	}

	@Override
	public Set<Method> getMethods() {
		Method method = new Method();
		method.setName("doSomething");
		Map<String, Class<?>> input = new HashMap<>();
		input.put("param1", Integer.class);
		method.setInput(input);
		method.setOutput(String.class);
		return new HashSet<>(Arrays.asList(method));
	}

	@Override
	public Map<String, Class<?>> getConfigurationSpecification() {
		Map<String, Class<?>> input = new HashMap<>();
		input.put("param1", Integer.class);
		return input;
	}


	@Override
	public Object execute(String domainId, String methodName, Map<String, Object> inputParams, Map<String,Object> configuration) {
		
		return String.format("executed for %s, %s with input %s", domainId, methodName, inputParams);
	}


}
