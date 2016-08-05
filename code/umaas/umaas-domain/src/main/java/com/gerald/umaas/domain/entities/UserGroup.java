package com.gerald.umaas.domain.entities;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.DBRef;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@CompoundIndexes({
    @CompoundIndex(name = "user_group", def = "{'user.$id' : 1, 'group.$id' : 1}", unique = true)
})
@EqualsAndHashCode(callSuper=true)
public class UserGroup extends Resource {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3896371055741103341L;
	@DBRef
	private AppUser user;
	@DBRef
	private Group group;
}
