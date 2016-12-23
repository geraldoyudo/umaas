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
public class SMSService extends AbstractCommunicationService {

	@Override
	public String getId() {
		return SMSService.class.getName();
	}

	@Override
	public String getName() {
		return "SMS";
	}

	@Override
	public Collection<TypeSpec> getConfigurationSpecification() {
		return Arrays.asList( 
				new TypeSpec("url", "Rest API URL", String.class),
				new TypeSpec("httpMethod", "HTTP Method", String.class),
				new TypeSpec("keysTo", "Destination Key (to)", String.class),
				new TypeSpec("keysFrom", "Origination Key (from)", String.class),
				new TypeSpec("keysBody", "Message Key (body)", String.class),
				new TypeSpec("username", "Username", String.class),
				new TypeSpec("password", "Password", String.class),
				new TypeSpec("authType", "Authentication Type (http-basic)", String.class),
				new TypeSpec("keysUser", "Username Key (username)", String.class),
				new TypeSpec("keysPassword", "Password Key (password)", String.class),
				new TypeSpec("format", "POST Format (json)", String.class),
				new TypeSpec("parser", "Parser", String.class),
				new TypeSpec("from", "Origination (APP)", String.class)
				);
	}

	@Override
	protected MessageData createMessage(Map<String, Object> input, Map<String, Object> configuration) {
		MessageData data = new MessageData();
		data.setType(MessageType.SMS);
		data.setFrom(configuration.get("from").toString());
		data.set("api", "single/rest/general");
		data.set("url", configuration.get("url").toString());
		data.set("httpMethod", configuration.get("url").toString());
		data.set("keys.to", configuration.getOrDefault("keysTo", "to").toString());
		data.set("keys.from", configuration.getOrDefault("keysFrom", "from").toString());
		data.set("keys.body", configuration.getOrDefault("keysBody", "body").toString());
		data.set("username", configuration.get("username").toString());
		data.set("password", configuration.get("password").toString());
		data.set("authType", configuration.getOrDefault("authType", "http-basic").toString());
		data.set("keys.user", configuration.getOrDefault("keysUser", "username").toString());
		data.set("keys.password", configuration.getOrDefault("keysPassword", "password").toString());
		data.set("format", configuration.getOrDefault("format", "json").toString());
		data.set("parser", configuration.getOrDefault("parser", "").toString());
		data.set("from", configuration.getOrDefault("from", "APP").toString());
		return data;
	}

	@Override
	protected void processMessageData(Map<String, Object> inputParams, Map<String, Object> configuration, AppUser user,
			MessageData data) {
		data.setTo(new String[]{user.getPhoneNumber()});
		VelocityContext c = new VelocityContext();
		c.put("user", user);
		StringWriter sw = new StringWriter();
		boolean okay = velocityEngine.evaluate(c, sw, EmailService.class.getName(),
				inputParams.getOrDefault("body", "").toString());
		if(!okay) throw new IllegalStateException("Invalid Message");
		data.setBody(sw.toString());
	}

	@Override
	protected void processMessageData(Map<String, Object> inputParams, Map<String, Object> configuration, String id,
			MessageData data) {
		data.setTo(new String[]{id});
		data.setBody(inputParams.getOrDefault("body", "").toString());	
	}
	
	
}
