package com.gerald.umaas.domain.entities.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.gerald.umaas.domain.entities.Field;

@Component
@Qualifier("generalFieldHandler")
public class FieldHandlerFacade implements UserFieldHandler {
	
	private Map<String, UserFieldHandler> fieldHandlerMap = new HashMap<>();
	
	@Autowired(required = false)
	public void setFieldHandlers(List<AbstractUserFieldHandler> fieldHandlers){
		if(fieldHandlers == null) return;
		for(AbstractUserFieldHandler fh: fieldHandlers){
			fieldHandlerMap.put(fh.getSupportedType(), fh);
		}
	}
	
	@Override
	public Object parse(Object value, Field field) {
		UserFieldHandler fh = fieldHandlerMap.get(field.getType());
		if(fh == null) return value;
		return fh.parse(value, field);
	}

	@Override
	public Object convert(Object value, Field field) {
		UserFieldHandler fh = fieldHandlerMap.get(field.getType());
		if(fh == null) return value;
		return fh.convert(value, field);
	}

	

}
