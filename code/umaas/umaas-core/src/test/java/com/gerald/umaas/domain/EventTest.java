package com.gerald.umaas.domain;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringRunner;

import com.gerald.umaas.domain.entities.AppEvent;
import com.gerald.umaas.domain.services.SystemEventHandler;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EventTest {
	@Autowired
	private SystemEventHandler handler;
	
	@Test
	public void testEventSending(){
		AppEvent event = new AppEvent();
		event.setType("test");
		event.set("user", "Gerald");
		event.set("age", 1);
		handler.onApplicationEvent(event);
	}
}
