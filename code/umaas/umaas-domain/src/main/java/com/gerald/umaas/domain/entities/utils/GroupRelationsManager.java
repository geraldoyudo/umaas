package com.gerald.umaas.domain.entities.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.AfterConvertEvent;
import org.springframework.data.mongodb.core.mapping.event.BeforeDeleteEvent;
import org.springframework.stereotype.Component;

import com.gerald.umaas.domain.entities.Affiliate;
import com.gerald.umaas.domain.entities.AppUser;
import com.gerald.umaas.domain.entities.Domain;
import com.gerald.umaas.domain.entities.Field;
import com.gerald.umaas.domain.entities.Group;
import com.gerald.umaas.domain.entities.Role;
import com.gerald.umaas.domain.entities.RoleMapping;
import com.gerald.umaas.domain.entities.UserField;
import com.gerald.umaas.domain.entities.UserGroup;
import com.gerald.umaas.domain.repositories.FieldRepository;
import com.gerald.umaas.domain.repositories.GroupRepository;
import com.gerald.umaas.domain.repositories.RoleMappingRepository;
import com.gerald.umaas.domain.repositories.UserFieldRepository;
import com.gerald.umaas.domain.repositories.UserGroupRepository;

@Component
public class GroupRelationsManager extends AbstractMongoEventListener<Group>{
	@Autowired
	private UserGroupRepository userGroupRepository;
	@Autowired
	private RoleMappingRepository roleMappingRepository;
	@Autowired
	private GroupRepository groupRepository;
	
	
	
	@Override
	public void onAfterConvert(AfterConvertEvent<Group> event) {
		Group g = event.getSource();
		populateRoles(g);
	}
	
	@Override
	public void onBeforeDelete(BeforeDeleteEvent<Group> event) {
		// TODO Auto-generated method stub\
		System.out.println("On Before Delete");
		String id = (String) event.getDBObject().get("id");
		Group g = groupRepository.findOne(id);
		roleMappingRepository.delete(roleMappingRepository.findByKeyAndType(g.getId(),g.type()));
		
	}
	
	private void populateRoles(Group g){
		Stack<Affiliate> affiliates = new Stack<>();
	
        affiliates.add(g);
        HashSet<Role> roles = new HashSet<>();
        RoleMapping mapping;
        Affiliate affiliate;
        String domain = g.getDomain().getId();
        Group group;
        do{
            affiliate = affiliates.pop();
            mapping = roleMappingRepository.findByDomainAndKeyAndType(domain, affiliate.key(),affiliate.type());
            if(mapping != null) 
               roles.addAll(mapping.getRoles());
            if(affiliate.type() == RoleMapping.RoleMappingType.GROUP){
               group = (Group) affiliate;
               while(group.getParent() != null){
                   group = group.getParent();
                   affiliate = group;
                   affiliates.remove(affiliate);
                   mapping = roleMappingRepository.findByDomainAndKeyAndType(domain, affiliate.key(),affiliate.type());
                   if(mapping != null) 
                      roles.addAll(mapping.getRoles());
               }
               
            }
           
        }while(!affiliates.isEmpty());
        
        Set<String> rolesInStr = new HashSet<>();
        for(Role r: roles){
            rolesInStr.add(r.getName());
        }
        g.setRoles(new ArrayList<>(rolesInStr));
	}
	
}
