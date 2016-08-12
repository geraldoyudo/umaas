package com.gerald.umaas.domain.entities.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeDeleteEvent;
import org.springframework.stereotype.Component;

import com.gerald.umaas.domain.entities.Field;
import com.gerald.umaas.domain.repositories.FieldRepository;
import com.gerald.umaas.domain.repositories.UserFieldRepository;

@Component
public class FieldRelationsManager extends AbstractMongoEventListener<Field>{
	@Autowired
	private UserFieldRepository userFieldRepository;
	
	@Autowired
	private FieldRepository fieldRepository;
	
	
	@Override
	public void onBeforeDelete(BeforeDeleteEvent<Field> event) {
		// TODO Auto-generated method stub\
		System.out.println("On Before Delete");
		String id = (String) event.getDBObject().get("id");
		Field f = fieldRepository.findOne(id);
		userFieldRepository.delete(userFieldRepository.findByField(f));
	}
	
	
}
