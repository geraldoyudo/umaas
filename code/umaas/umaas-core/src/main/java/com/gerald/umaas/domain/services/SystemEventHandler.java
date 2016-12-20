package com.gerald.umaas.domain.services;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gerald.umaas.domain.entities.AppEvent;
import com.gerald.umaas.domain.repositories.AppEventRepository;

@Component
public class SystemEventHandler{
	@Autowired
	private AppEventRepository eventRepository;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private JmsTemplate jmsTemplate;
	private static final String NAMESPACE = "umaas-event";
	@EventListener
	public void onApplicationEvent(AppEvent event) {
		event.setDate(new Date());
		AppEvent e = eventRepository.save(event);
		jmsTemplate.convertAndSend(String.format("%s.%s.topic", NAMESPACE, e.getType()), event);
	}

}
