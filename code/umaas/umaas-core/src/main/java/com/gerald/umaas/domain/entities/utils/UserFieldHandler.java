package com.gerald.umaas.domain.entities.utils;

import com.gerald.umaas.domain.entities.Field;

public interface UserFieldHandler {
	public Object parse(Object value, Field field);
	public Object convert(Object value, Field field );
}
