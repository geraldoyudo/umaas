package com.gerald.umaas.domain.entities.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.gerald.umaas.domain.entities.AppUser;
import com.gerald.umaas.domain.entities.Field;
import com.gerald.umaas.domain.entities.UserField;
import com.gerald.umaas.domain.repositories.FieldRepository;
import com.gerald.umaas.domain.repositories.UserFieldRepository;

@Component
@Aspect
public class UserPropertiesPopulator {
	@Autowired
	private UserFieldRepository userFieldRepository;
	@Autowired
	private FieldRepository fieldRepository;
	
	@Around("execution(*"
			+ " com.gerald.umaas.domain.repositories.UserRepository.save(..))")
	@Transactional
	private Object saveUser(ProceedingJoinPoint pjp) throws Throwable{
		Object input = pjp.getArgs()[0];
		if(input instanceof AppUser){
			System.out.println(" User properties reading ");
			AppUser user = (AppUser) input;
			System.out.println(user);
			System.out.println(user.getProperties());
			List<Field> fields = fieldRepository.findByDomain(user.getDomain().getId());
			ArrayList<String> keySet = new ArrayList<>(user.getProperties().keySet());
			Map<String,Object> properties = user.getProperties();
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
		}else if(input instanceof List){
			System.out.println(" User properties list reading ");
			List<AppUser> users = (List<AppUser> ) input;
			List<Field> fields;
			ArrayList<String> keySet;
			UserField userField;
			Map<String,Object> properties;
			for(AppUser user: users){
			    fields = fieldRepository.findByDomain(user.getDomain().getId());
				keySet = new ArrayList<>(user.getProperties().keySet());
				 properties = user.getProperties();
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
		}else{
			// ignore
		}
		return pjp.proceed();
	}
	
	@Around("execution(*"
			+ " com.gerald.umaas.domain.repositories.UserRepository.find*(..))")
	public Object populateUser(ProceedingJoinPoint pjp) throws Throwable{
		
		Object retVal = pjp.proceed();
			if(retVal instanceof AppUser){
			 System.out.println(" User properties populating ");
			 AppUser u = (AppUser) pjp.proceed();
			 List<UserField> userFields = userFieldRepository.findByUser(u);
			 HashMap<String,Object> properties = new HashMap<String,Object> ();
			 for(UserField userField: userFields){
				 properties.put(userField.getField().getName(), userField.getValue());
				 u.setProperties(properties);
			 }
			 return u;
		}else if( retVal instanceof List){
			System.out.println(" User properties list populating ");
			List<AppUser> users = (List<AppUser>) pjp.proceed();
			 for(AppUser u: users){
				 List<UserField> userFields = userFieldRepository.findByUser(u);
				 HashMap<String,Object> properties = new HashMap<String,Object> ();
				 for(UserField userField: userFields){
					 properties.put(userField.getField().getName(), userField.getValue());
					 u.setProperties(properties);
				 }
			 }
			 return users;
		}else if ( retVal instanceof Page){
			System.out.println(" User properties page populating ");
			Page<AppUser> users = (Page<AppUser>) pjp.proceed();
			 for(AppUser u: users){
				 List<UserField> userFields = userFieldRepository.findByUser(u);
				 HashMap<String,Object> properties = new HashMap<String,Object> ();
				 for(UserField userField: userFields){
					 properties.put(userField.getField().getName(), userField.getValue());
					 u.setProperties(properties);
				 }
			 }
			 return users;
		}else{
			return retVal;
		}
		 
	}
	
}
