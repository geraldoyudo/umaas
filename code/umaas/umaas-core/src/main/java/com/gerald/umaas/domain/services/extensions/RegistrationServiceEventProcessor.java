package com.gerald.umaas.domain.services.extensions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.gerald.umaas.domain.entities.AppEvent;
import com.gerald.umaas.domain.entities.Domain;
import com.gerald.umaas.domain.services.CustomDomainServiceProxy;

@Component
public class RegistrationServiceEventProcessor {
	@Autowired
	private CustomDomainServiceProxy serviceProxy;
	
	@Order(1000)
	@EventListener(condition = "#event.type.equals(T(com.gerald.umaas.domain.services.Events).REGISTRATION_SUCCESS)")
	public void prepareRegistrationSuccesss(AppEvent event){
		System.out.println("prepareRegistrationSuccess");
		Domain d = event.getDomain();
		if(d == null) return;
		Map<String,Object> configuration = serviceProxy.getConfiguration(RegistrationService.class.getName(),
				d.getId());
		try{
			List<String> compile = new ArrayList<>();
			boolean send =(boolean) configuration.get("sendRegistrationSuccessMail");
			if(!send) return;
			compile.add("mailSubject");
			compile.add("mailBody");
			event.set("mailSubject", configuration.get("registrationSuccessSubject"));
			event.set("mailBody", configuration.get("registrationSuccessBody"));
			event.set("compile", compile);
		}catch(NullPointerException ex){
			return;
		}
	}
	
	@Order(1000)
	@EventListener(condition = "#event.type.equals(T(com.gerald.umaas.domain.services.Events).PASSWORD_RESET_SUCCESS)")
	public void preparePasswordResetSuccess(AppEvent event){
		System.out.println("preparePasswordResetSuccess");
		Domain d = event.getDomain();
		if(d == null) return;
		Map<String,Object> configuration = serviceProxy.getConfiguration(RegistrationService.class.getName(),
				d.getId());
		List<String> compile = new ArrayList<>();
		boolean send =(boolean) configuration.get("sendPasswordResetSuccessMail");
		if(!send) return;
		compile.add("mailSubject");
		compile.add("mailBody");
		event.set("mailSubject", configuration.get("passwordResetSuccessSubject"));
		event.set("mailBody", configuration.get("passwordResetSuccessBody"));
		event.set("compile", compile);
	}
}
