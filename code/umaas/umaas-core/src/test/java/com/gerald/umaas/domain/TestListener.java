package com.gerald.umaas.domain;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.gerald.umaas.domain.entities.AppEvent;

@Component
public class TestListener{
	@JmsListener( destination = "umaas-event.test.topic")
	public void onEvent(AppEvent event){
		System.out.println("First Listener");
		System.out.println(event);
	}
	
	@JmsListener( destination = "umaas-event.test.topic")
	public void onEvent1(AppEvent event){
		System.out.println("Second Listener");
		System.out.println(event);
	}
}