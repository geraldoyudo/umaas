package com.gerald.umaas.domain.entities.utils;

import com.gerald.umaas.domain.entities.Field;

public abstract class AbstractUserFieldHandler implements UserFieldHandler {

	public abstract String getSupportedType();
	public abstract Object onConvert(Object value, Field f);
	public abstract Object onParse(Object value, Field f);

	@Override
	public Object parse(Object value, Field f) {
		String type = getSupportedType();
		if(type == null || !type.equals(f.getType()))
			throw new IllegalArgumentException("Cannot convert value to " + f.getType());
		return onParse(value, f);
	}

	@Override
	public Object convert(Object value, Field f) {
		String type = getSupportedType();
		if(type == null || !type.equals(f.getType()))
			throw new IllegalArgumentException("Cannot convert value to " + f.getType());
		return onConvert(value, f);
	}

}
