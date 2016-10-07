package com.gerald.umaas.domain.entities.utils;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.gerald.umaas.domain.entities.AppUser;
import com.gerald.umaas.domain.entities.Domain;
import com.gerald.umaas.domain.entities.Field;
import com.gerald.umaas.domain.repositories.FieldRepository
;
@Component
public class UserFieldValidator {
	@Autowired
	private FieldRepository fieldRepository;
	@Autowired
	@Qualifier("generalFieldHandler")
	private UserFieldHandler userFieldHandler;
	
	public void validate(AppUser user){
		Domain d = user.getDomain();
		if(d == null)
			throw new UserFieldValidationException("No domain set!");
		Map<String, Object> properties = user.getProperties();
		String fieldName = "";
		for(Field f: fieldRepository.findByDomain(d.getId())){
			fieldName = f.getName();
			properties.put(fieldName, userFieldHandler.parse(properties.get(fieldName), f));
		}
	}
	
	public void format(AppUser user){
		Domain d = user.getDomain();
		if(d == null)
			return;
		Map<String, Object> properties = user.getProperties();
		String fieldName = "";
		for(Field f: fieldRepository.findByDomain(d.getId())){
			fieldName = f.getName();
			properties.put(fieldName, userFieldHandler.convert(properties.get(fieldName), f));
		}
	}
}
