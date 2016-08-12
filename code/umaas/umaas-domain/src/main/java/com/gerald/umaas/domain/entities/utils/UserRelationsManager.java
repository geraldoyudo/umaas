package com.gerald.umaas.domain.entities.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.AfterConvertEvent;
import org.springframework.data.mongodb.core.mapping.event.AfterSaveEvent;
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
import com.gerald.umaas.domain.repositories.UserRepository;

@Component
public class UserRelationsManager extends AbstractMongoEventListener<AppUser>{
	@Autowired
	private UserFieldRepository userFieldRepository;
	@Autowired
	private UserGroupRepository userGroupRepository;
	@Autowired
	private RoleMappingRepository roleMappingRepository;
	@Autowired
	private GroupRepository groupRepository;
	@Autowired
	private FieldRepository fieldRepository;
	@Autowired
	private UserRepository userRepository;
	
	@Override
	public void onAfterSave(AfterSaveEvent<AppUser> event) {
		System.out.println("On after save");
		AppUser u = event.getSource();
		saveUserProperties(u.getProperties(), u);
		saveUserGroups(u.getGroups(), u);
	}
	
	
	@Override
	public void onAfterConvert(AfterConvertEvent<AppUser> event) {
		System.out.println("On after convert");
		AppUser u = event.getSource();
		populateProperties(u);
		populateGroups(u);
		populateRoles(u);
		super.onAfterConvert(event);
	}
	
	@Override
	public void onBeforeDelete(BeforeDeleteEvent<AppUser> event) {
		String id = (String) event.getDBObject().get("id");
		System.out.println(id);
		AppUser u = userRepository.findOne(id);
		userFieldRepository.delete(userFieldRepository.findByUser(u));
		roleMappingRepository.delete(roleMappingRepository.findByKeyAndType(u.getId(),u.type()));
		userGroupRepository.delete(userGroupRepository.findByUser(u));
	
	}
	
	private void populateRoles(AppUser u){
		Stack<Affiliate> affiliates = new Stack<>();
		List<UserGroup> userGroups = userGroupRepository.findByUser(u);
		for(UserGroup ug: userGroups){
			affiliates.add(ug.getGroup());
		}
        affiliates.add(u);
        HashSet<Role> roles = new HashSet<>();
        RoleMapping mapping;
        Affiliate affiliate;
        String domain = u.getDomain().getId();
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
        u.setRoles(new ArrayList<>(rolesInStr));
	}
	
	private void populateProperties(AppUser u) {
		List<UserField> userFields = userFieldRepository.findByUser(u);
		 HashMap<String,Object> properties = new HashMap<String,Object> ();
		 for(UserField userField: userFields){
			 properties.put(userField.getField().getName(), userField.getValue());	
		 }
		 u.setProperties(properties);
	}
	
	private void populateGroups(AppUser u) {
		List<UserGroup> userGroups = userGroupRepository.findByUser(u);
		List<String> groups = new ArrayList<>();
		 for(UserGroup userGroup: userGroups){
			 groups.add(userGroup.getGroup().getName());
		 }
		 u.setGroups(groups);
	}
	
	private void saveUserProperties(Map<String,Object> properties, AppUser user) {
		//System.out.println(user);
		List<Field> fields = fieldRepository.findByDomain(user.getDomain().getId());
		ArrayList<String> keySet = new ArrayList<>(user.getProperties().keySet());
		
		UserField userField;
		for(Field field: fields){
			if(keySet.contains(field.getName())){
				userField = userFieldRepository.findByUserAndField(user, field);
				if(userField ==null){
					userField = new UserField();
					userField.setUser(user);
					userField.setField(field);
				}
				userField.setValue(properties.get(field.getName()));
				userFieldRepository.save(userField);
			}
		}
	}
	private void saveUserGroups (List<String> groups, AppUser user) {
		System.out.println(groups);
		Domain d = user.getDomain();
		List<UserGroup> userGroups = userGroupRepository.findByUser(user);
		// delete groups that are not on the list
		String group;
		for(UserGroup userGroup: userGroups){
			group = userGroup.getGroup().getName();
			if(!groups.contains(group)){
				userGroupRepository.delete(userGroup);
			}else{
				groups.remove(group); // remove from list of groups to add
			}
		}
		// add groups in list
		Group g;
		for(String groupName: groups){
			g = groupRepository.findByDomainAndName(d, groupName);
			if(g == null) continue;
			UserGroup userGroup = new UserGroup();
			userGroup.setUser(user);
			userGroup.setGroup(g);
			userGroupRepository.save(userGroup);
		}
		
	}
	
}
