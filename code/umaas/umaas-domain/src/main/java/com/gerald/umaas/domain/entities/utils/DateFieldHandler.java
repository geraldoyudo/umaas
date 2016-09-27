package com.gerald.umaas.domain.entities.utils;


import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.annotation.PostConstruct;

import org.springframework.format.datetime.DateFormatter;
import org.springframework.stereotype.Component;

import com.gerald.umaas.domain.entities.Field;

@Component
public class DateFieldHandler extends AbstractUserFieldHandler{
	
	private DateFormatter dateFormatter = new DateFormatter();
	private DateFormatter backUpFormatter = new DateFormatter();
	
	@PostConstruct
	public void init(){
		//dateFormatter.setPattern("yyyy-MM-ddTHH:mm:ss.SSSZ");
	}
	
	@Override
	public String getSupportedType() {
		return "date";
	}

	@Override
	public Object onConvert(Object value, Field f) {
		if(value == null) return null;
		if(value instanceof Date){
			Date date = (Date) value;
			return date;
		}else{
			return onParse(value, f);
		}
	}

	@Override
	public Object onParse(Object value, Field f) {
		if(value == null) return null;
		if(value instanceof Date)
			return value;
		String valStr = value.toString();
		try {
			  Calendar calendar = javax.xml.bind.DatatypeConverter.parseDateTime(valStr);
			 return calendar.getTime();
		} catch (IllegalArgumentException e) {
			try{
			Date date = backUpFormatter.parse(valStr, Locale.ENGLISH);
			return date;
			}catch(Exception ex){
				///ex.printStackTrace();
				return null;
			}
			
		}catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}

}
