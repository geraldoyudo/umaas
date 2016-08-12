package com.gerald.umaas.domain.entities;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.DBRef;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@CompoundIndexes({
    @CompoundIndex(name = "user_field", def = "{'user.$id' : 1, 'field.$id' : 1}", unique = true)
})
@EqualsAndHashCode(callSuper=true)
public class UserField extends DomainResource {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6764855316973954605L;
	@DBRef
	private AppUser user;
	@DBRef
	private Field field;
	private Object value;
	
}
