package com.gerald.umaas.domain.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.gerald.umaas.domain.entities.AppEvent;
import com.gerald.umaas.domain.entities.Domain;

@Component
public class EventLogger{
	private static final Logger log = LoggerFactory
			.getLogger(EventLogger.class);
	
	@JmsListener( destination = "${app.namespace}.manager.*.*.topic")
	public void onEvent(AppEvent event){
		Domain d = event.getDomain();
		String domainName = "NULL";
		if(d != null ){
			domainName = String.format("%s (%s)", d.getName(), d.getId());
		}
		log.info("Event Type {} Received. Domain = {}; Data = {}",
				event.getType(), domainName, event.getData());
		
	}
}