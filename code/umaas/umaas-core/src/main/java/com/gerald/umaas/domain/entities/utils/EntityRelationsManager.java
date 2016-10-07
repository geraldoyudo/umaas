package com.gerald.umaas.domain.entities.utils;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gerald.umaas.domain.entities.AppUser;
import com.gerald.umaas.domain.entities.Field;
import com.gerald.umaas.domain.entities.Group;
import com.gerald.umaas.domain.entities.Role;
import com.gerald.umaas.domain.repositories.FieldRepository;
import com.gerald.umaas.domain.repositories.GroupRepository;
import com.gerald.umaas.domain.repositories.RoleMappingRepository;
import com.gerald.umaas.domain.repositories.UserFieldRepository;
import com.gerald.umaas.domain.repositories.UserGroupRepository;

@Component

public class EntityRelationsManager {
	@Autowired
	private UserFieldRepository userFieldRepository;
	@Autowired
	private FieldRepository fieldRepository;
	@Autowired
	private UserGroupRepository userGroupRepository;
	@Autowired
	private GroupRepository groupRepository;
	@Autowired
	private RoleMappingRepository roleMappingRepository;
	
    @Before(  "execution(*"
			+ " com.gerald.umaas.domain.repositories.UserRepository.delete(..))")
    public void beforeDeleteUser(JoinPoint jp) {
    	
        // ...
    }
	@Before("com.xyz.myapp.SystemArchitecture.dataAccessOperation()")
    public void beforeDeleteField() {
        // ...
    }
	@Before("com.xyz.myapp.SystemArchitecture.dataAccessOperation()")
    public void beforeDeleteRole() {
        // ...
    }
	
	
	
	public void prepareDeleteUser(AppUser user){
		
	}
	
	public void prepareDeleteGroup(Group group){
		
	}
	
	public void prepareDeleteRole(Role role){
		
	}
	
	public void prepareDeleteField(Field field){
		
	}
}
