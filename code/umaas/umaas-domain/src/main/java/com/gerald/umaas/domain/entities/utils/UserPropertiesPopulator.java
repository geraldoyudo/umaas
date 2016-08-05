package com.gerald.umaas.domain.entities.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
@Aspect
public class UserPropertiesPopulator {
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
	
	@Around("execution(*"
			+ " com.gerald.umaas.domain.repositories.UserRepository.save(..))")
	@Transactional
	private Object saveUser(ProceedingJoinPoint pjp) throws Throwable{
		Object input = pjp.getArgs()[0];
		Object output = pjp.proceed();
		if(output instanceof AppUser){
			System.out.println(" User group reading ");
			AppUser user = (AppUser) output;
			saveUserProperties(((AppUser)input).getProperties(), user);
			saveUserGroups(((AppUser)input).getGroups(), user);
		}else if(input instanceof List){
			System.out.println(" User group list reading ");
			List<AppUser> users = (List<AppUser> ) output;
			List<AppUser> inputUsers = (List<AppUser>)input;
			for(int i=0; i< users.size(); ++i){
				saveUserProperties(inputUsers.get(i).getProperties(), users.get(i));
				saveUserGroups(inputUsers.get(i).getGroups(), users.get(i));
			}
		}else{
			// ignore
		}
		return output;
	}

	@Around("execution(*"
			+ " com.gerald.umaas.domain.repositories.UserRepository.find*(..))")
	public Object populateUser(ProceedingJoinPoint pjp) throws Throwable{
		
		Object retVal = pjp.proceed();
			if(retVal instanceof AppUser){
			 System.out.println(" User properties populating ");
			 AppUser u = (AppUser) pjp.proceed();
			 populateProperties(u);
			 populateGroups(u);
			 populateRoles(u);
			 return u;
		}else if( retVal instanceof List){
			System.out.println(" User properties list populating ");
			List<AppUser> users = (List<AppUser>) pjp.proceed();
			 for(AppUser u: users){
				 populateProperties(u);
				 populateGroups(u);
				 populateRoles(u);
			 }
			 return users;
		}else if ( retVal instanceof Page){
			System.out.println(" User properties page populating ");
			Page<AppUser> users = (Page<AppUser>) pjp.proceed();
			 for(AppUser u: users.getContent()){
				 populateProperties(u);
				 populateGroups(u);
				 populateRoles(u);
			 }
			 return users;
		}else{
			return retVal;
		}
		 
	}
	@Around("execution(*"
			+ " com.gerald.umaas.domain.repositories.GroupRepository.find*(..))")
	public Object populateGroup(ProceedingJoinPoint pjp) throws Throwable{
		
		Object retVal = pjp.proceed();
			if(retVal instanceof Group){
			 System.out.println(" User properties populating ");
			 Group g = (Group) pjp.proceed();
			 populateRoles(g);
			 return g;
		}else if( retVal instanceof List){
			System.out.println(" User properties list populating ");
			List<Group> groups = (List<Group>) pjp.proceed();
			 for(Group g: groups){
				 populateRoles(g);
			 }
			 return groups;
		}else if ( retVal instanceof Page){
			System.out.println(" User properties page populating ");
			Page<Group> groups = (Page<Group>) pjp.proceed();
			 for(Group g: groups.getContent()){
				 populateRoles(g);
			 }
			 return groups;
		}else{
			return retVal;
		}
		 
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
