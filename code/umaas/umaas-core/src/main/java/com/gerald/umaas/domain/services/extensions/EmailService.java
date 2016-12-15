package com.gerald.umaas.domain.services.extensions;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.apache.velocity.VelocityContext;
import org.springframework.stereotype.Component;

import com.gerald.umaas.domain.entities.AppUser;
import com.gerald.umaas.domain.services.extensions.MessageData.MessageType;
import com.gerald.umaas.extensionpoint.TypeSpec;

@Component
public class EmailService extends AbstractCommunicationService {

	
	@Override
	public String getId() {
		return EmailService.class.getName();
	}

	@Override
	public String getName() {
		return "Email";
	}


	@Override
	public Collection<TypeSpec> getConfigurationSpecification() {
		return Arrays.asList( 
				new TypeSpec("subject", "Default Subject", String.class),
				new TypeSpec("host", "Host (and Port)", String.class),
				new TypeSpec("username", "Username", String.class),
				new TypeSpec("password", "Password", String.class),
				new TypeSpec("protocol", "Protocol", String.class)
				);
	}

	
	@Override
	protected MessageData createMessage(Map<String,Object> input, Map<String,Object> configuration){
		MessageData data = new MessageData();
		data.setFrom(configuration.get("username").toString());
		data.setType(MessageType.EMAIL);
		data.set("protocol", configuration.get("protocol"));
		data.set("host", configuration.get("host"));
		data.set("password", configuration.get("password"));
		data.set("username", configuration.get("username"));
		return data;
	}
	
	

	@Override
	protected void processMessageData(Map<String, Object> inputParams, Map<String, Object> configuration, AppUser user,
			MessageData data) {
		data.setTo(new String[]{user.getEmail()});
		VelocityContext c = new VelocityContext();
		c.put("user", user);
		StringWriter sw = new StringWriter();
		boolean okay = velocityEngine.evaluate(c, sw, EmailService.class.getName(),
				inputParams.getOrDefault("body", "").toString());
		if(!okay) throw new IllegalStateException("Invalid Message");
		data.setBody(sw.toString());
		String subjectTemplate =  inputParams.getOrDefault("subject", configuration.getOrDefault("subject", "")).toString();
		sw = new StringWriter();
		okay = velocityEngine.evaluate(c, sw, EmailService.class.getName(),
				subjectTemplate);
		if(!okay)  throw new IllegalStateException("Invalid Subject");
		data.set("subject", sw.toString());
	}

}
