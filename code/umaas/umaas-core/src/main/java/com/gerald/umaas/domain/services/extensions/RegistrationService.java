package com.gerald.umaas.domain.services.extensions;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.gerald.umaas.domain.entities.AppEvent;
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
		Map<String,String> textAreaProperties = new HashMap();
		textAreaProperties.put("inputType", "richtext");
		return Arrays.asList(new  TypeSpec(
				"afterRegistrationUrl", "Redirect to URL after Registration", String.class),
				new  TypeSpec(
						"loginPage", "Login Page", String.class),
				new  TypeSpec(
						"registrationTitle", "Title on Registration Page", String.class),
				new  TypeSpec(
						"passwordResetTitle", "Title on Password Reset Page", String.class),
				new  TypeSpec(
						"emailAsUsername", "Use Email As Username", Boolean.class),
				new  TypeSpec(
						"lockOnRegistration", "Lock On Registration", Boolean.class),
				new  TypeSpec(
						"verifyEmail", "Verify Email", Boolean.class),
				new  TypeSpec(
						"verifyPhoneNumber", "Verify Phone Number", Boolean.class),
				new  TypeSpec(
						"registrationSubject", "Registration Email Subject", String.class),
				new  TypeSpec(
						"passwordResetSubject", "Password Reset Subject", String.class),
				new  TypeSpec(
						"sendRegistrationSuccessMail", "Send Mail on Registration Success", Boolean.class),
				new  TypeSpec(
						"sendPasswordResetSuccessMail", "Send Mail on Password Reset Success", Boolean.class),
				new  TypeSpec(
						"registrationSuccessSubject", "Registration Success Email Subject", String.class),
				new  TypeSpec(
						"registrationSuccessBody", "Registration Success Email Body", String.class, textAreaProperties),
				new  TypeSpec(
						"passwordResetSuccessSubject", "Password Reset Success Email Subject", String.class),
				new  TypeSpec(
						"passwordResetSuccessBody", "Password Reset Success Email Body", String.class, textAreaProperties));
				
	}

	@Override
	public Object execute(String domainId, String methodName, Map<String, Object> inputParams,
			Map<String, Object> configuation) {
		throw new IllegalStateException("Not Supported");
	}

}
