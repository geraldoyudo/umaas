package com.gerald.umaas.domain.services;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.gerald.umaas.extensionpoint.CustomDomainService;
import com.gerald.umaas.extensionpoint.Method;
import com.gerald.umaas.extensionpoint.TypeSpec;

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
		TypeSpec spec = new  TypeSpec(
				"param1", "First Parameter", Integer.class);
		method.setInput( Arrays.asList(spec));
		method.setOutput(String.class);
		return new HashSet<>(Arrays.asList(method));
	}

	@Override
	public Collection<TypeSpec> getConfigurationSpecification() {
		TypeSpec spec = new  TypeSpec(
				"param1", "First Parameter", Integer.class);
		return Arrays.asList(spec);
	}


	@Override
	public Object execute(String domainId, String methodName, Map<String, Object> inputParams, Map<String,Object> configuration) {
		
		return String.format("executed for %s, %s with input %s", domainId, methodName, inputParams);
	}


}
