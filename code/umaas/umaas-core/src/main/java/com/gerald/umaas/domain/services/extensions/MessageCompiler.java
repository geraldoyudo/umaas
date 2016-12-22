package com.gerald.umaas.domain.services.extensions;

import java.io.StringWriter;
import java.util.List;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.gerald.umaas.domain.entities.AppEvent;
import com.gerald.umaas.domain.entities.AppUser;
import com.gerald.umaas.domain.repositories.UserRepository;

@Component
public class MessageCompiler {
	@Autowired
	private VelocityEngine velocityEngine;
	@Autowired
	private UserRepository userRepository;
	
	@Order(Ordered.LOWEST_PRECEDENCE - 1000)
	@EventListener
	public void compileMessage(AppEvent event){
		System.out.println("compileMessage");
		List<String> compileProperties =(List<String>) event.get("compile");
		if(compileProperties == null || compileProperties.isEmpty()) return;
		for(String prop: compileProperties){
			try{
			String message = event.get(prop).toString();
			message = compile(message, event);
			event.set(prop, message);
			}catch (NullPointerException ex){
				continue;
			}
		}
	}

	private String compile(String message, AppEvent event) {
		VelocityContext c = new VelocityContext();
		c.put("data", event.getData());
		c.put("domain", event.getDomain());
		Object userId = event.get("userId");
		if(userId != null){
			AppUser user = userRepository.findOne(userId.toString());
			if(user != null){
				c.put("user", user);
			}
		}
		StringWriter sw = new StringWriter();
		boolean okay = velocityEngine.evaluate(c, sw, EmailService.class.getName(),
				message);
		if(!okay) throw new IllegalStateException("Invalid Message");
		return sw.toString();
	}
}
