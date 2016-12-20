package com.gerald.umaas.domain.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.gerald.umaas.domain.entities.AppEvent;
import com.gerald.umaas.domain.entities.Domain;
import com.gerald.umaas.domain.repositories.DomainRepository;
import com.gerald.umaas.domain.web.utils.EventInput;

@RestController
public class AppEventController {
	@Autowired
	private ApplicationEventPublisher publisher;
	@Autowired
	private DomainRepository domainRepository;
	
	@PostMapping(path = {"/event/{domainId}"})
	public void sendEvent(@PathVariable(value= "domainId") String domainId, 
			@RequestBody EventInput input){
		Domain d = domainRepository.findOne(domainId);
		if(d == null){
			throw new NullPointerException("Domain not found");
		}
		AppEvent e = new AppEvent();
		e.setDomain(d);
		e.setType(input.getType());
		e.setData(input.getData());
		publisher.publishEvent(e);
	}
	
	@PostMapping(path = {"/event"})
	public void sendSystemEvent(@RequestBody EventInput input){
		AppEvent e = new AppEvent();
		e.setDomain(null);
		e.setType(input.getType());
		e.setData(input.getData());
		publisher.publishEvent(e);
	}
}
