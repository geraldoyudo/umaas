package com.gerald.umaas.domain.entities;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

@Data
@CompoundIndexes({
    @CompoundIndex(name = "user_field", def = "{'user.$id' : 1, 'field.$id' : 1}", unique = true)
})
@EqualsAndHashCode(callSuper=true)
@Document
public class UserField extends DomainResource {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6764855316973954605L;
	@DBRef
	private AppUser user;
	@DBRef
	private Field field;
	@Setter
	private Object value;
	
	public Object getValue(){
		if(value == null){
			return field.getDefaultValue();
		}else{
			return value;
		}
	}
}
