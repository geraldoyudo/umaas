package com.gerald.umaas.domain.services.extensions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.annotation.JmsListeners;
import org.springframework.stereotype.Component;

import com.gerald.umaas.domain.entities.AppEvent;
import com.gerald.umaas.domain.entities.Domain;
import com.gerald.umaas.domain.security.ApiSecurityChecker;
import com.gerald.umaas.domain.services.CustomDomainServiceProxy;

@Component
public class RegistrationServiceEventProcessor {
	@Autowired
	private CustomDomainServiceProxy serviceProxy;
	private static final Logger log = LoggerFactory
			.getLogger(RegistrationServiceEventProcessor.class);
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
	
	@JmsListeners({
		@JmsListener( destination = "umaas-event.*.com.isslng.registration.success.topic"),
		@JmsListener( destination = "umaas-event.*.com.isslng.reset.success.topic")
	})
	public void sendMail(AppEvent event){
		Domain d = event.getDomain();
		if(d == null) return;
		
		try{
			Map<String,Object> input = new HashMap<>();
			input.put("id",event.get("mail").toString());
			input.put("body",event.get("mailBody"));
			input.put("subject", event.get("mailSubject"));
			serviceProxy.execute(EmailService.class.getName(),d.getId(),"send", input);
		}catch(NullPointerException ex){
			log.info("sendMail: Field is null returning");
		}
	}
}
