package com.gerald.umaas.domain.entities.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeDeleteEvent;
import org.springframework.stereotype.Component;

import com.gerald.umaas.domain.entities.Field;
import com.gerald.umaas.domain.entities.Role;
import com.gerald.umaas.domain.repositories.RoleMappingRepository;
import com.gerald.umaas.domain.repositories.RoleRepository;

@Component
public class RoleRelationsManager extends AbstractMongoEventListener<Field>{
	@Autowired
	private RoleMappingRepository roleMappingRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	
	@Override
	public void onBeforeDelete(BeforeDeleteEvent<Field> event) {
		// TODO Auto-generated method stub\
		System.out.println("On Before Delete");
		String id = (String) event.getDBObject().get("id");
		Role role = roleRepository.findOne(id);
		roleMappingRepository.delete(roleMappingRepository.findByRole(role));
		
	}
	
	
}
