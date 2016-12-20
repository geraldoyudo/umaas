package com.gerald.umaas.domain;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.junit4.SpringRunner;

import com.gerald.umaas.domain.entities.AppEvent;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EventTest {
	@Autowired
	private ApplicationEventPublisher publisher;
	
	@Test
	public void testEventSending(){
		AppEvent event = new AppEvent();
		event.setType("test");
		event.set("user", "Gerald");
		event.set("age", 1);
		publisher.publishEvent(event);
	}
}
