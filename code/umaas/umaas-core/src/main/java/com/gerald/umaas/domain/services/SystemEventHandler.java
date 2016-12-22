package com.gerald.umaas.domain.services;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gerald.umaas.domain.entities.AppEvent;
import com.gerald.umaas.domain.entities.Domain;
import com.gerald.umaas.domain.repositories.AppEventRepository;

@Component
public class SystemEventHandler{
	@Autowired
	private AppEventRepository eventRepository;
	@Autowired
	private JmsTemplate jmsTemplate;
	private static final String NAMESPACE = "umaas-event";
	
	@Order(Ordered.LOWEST_PRECEDENCE)
	@EventListener
	public void onApplicationEvent(AppEvent event) {
		event.setDate(new Date());
		Domain d = event.getDomain();
		String domainId= "system";
		if(d != null){
			domainId = d.getId();
		}
		AppEvent e = eventRepository.save(event);
		jmsTemplate.convertAndSend(String.format("%s.%s.%s.topic", NAMESPACE, domainId, 
				e.getType()), event);
	}

}
