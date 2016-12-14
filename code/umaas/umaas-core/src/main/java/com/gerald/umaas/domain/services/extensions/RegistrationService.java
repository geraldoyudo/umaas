package com.gerald.umaas.domain.services.extensions;

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
public class RegistrationService  implements CustomDomainService{

	@Override
	public String getId() {
		return RegistrationService.class.getName();
	}

	@Override
	public String getName() {
		return "UMAAS Registration Service";
	}

	@Override
	public Set<Method> getMethods() {
		return new HashSet<>();
	}

	@Override
	public Collection<TypeSpec> getConfigurationSpecification() {
		TypeSpec spec = new  TypeSpec(
				"registrationUrl", "Redirect to URL after Registration", String.class);
		return Arrays.asList(spec);
	}

	@Override
	public Object execute(String domainId, String methodName, Map<String, Object> inputParams,
			Map<String, Object> configuation) {
		throw new IllegalStateException("Not Supported");
	}

}
