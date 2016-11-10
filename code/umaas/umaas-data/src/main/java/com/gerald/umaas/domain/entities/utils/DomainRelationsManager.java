package com.gerald.umaas.domain.entities.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeDeleteEvent;
import org.springframework.stereotype.Component;

import com.gerald.umaas.domain.entities.Domain;
import com.gerald.umaas.domain.repositories.FieldRepository;
import com.gerald.umaas.domain.repositories.GroupRepository;
import com.gerald.umaas.domain.repositories.RoleMappingRepository;
import com.gerald.umaas.domain.repositories.RoleRepository;
import com.gerald.umaas.domain.repositories.UserFieldRepository;
import com.gerald.umaas.domain.repositories.UserGroupRepository;
import com.gerald.umaas.domain.repositories.UserRepository;

@Component
public class DomainRelationsManager extends AbstractMongoEventListener<Domain>{
	@Autowired
	private UserFieldRepository userFieldRepository;
	@Autowired
	private RoleMappingRepository roleMappingRepository;
	@Autowired
	private UserGroupRepository userGroupRepository;
	@Autowired
	private GroupRepository groupRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private FieldRepository fieldRepository;
	
	
	@Override
	public void onBeforeDelete(BeforeDeleteEvent<Domain> event) {
		// TODO Auto-generated method stub\
		System.out.println("On Before Delete");
		String id = (String) event.getDBObject().get("id");
		roleMappingRepository.delete(roleMappingRepository.findByDomain(id));
		userGroupRepository.delete(userGroupRepository.findByDomain(id));
		userFieldRepository.delete(userFieldRepository.findByDomain(id));
		roleRepository.delete(roleRepository.findByDomain(id));
		groupRepository.delete(groupRepository.findByDomain(id));
		fieldRepository.delete(fieldRepository.findByDomain(id));
		userRepository.delete(userRepository.findByDomain(id));
	}
	
	
}
