package com.gerald.umaas.domain.entities;

import javax.validation.constraints.NotNull;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@CompoundIndexes({
    @CompoundIndex(name = "user_group", def = "{'user.$id' : 1, 'group.$id' : 1}", unique = true)
})
@EqualsAndHashCode(callSuper=true)
@Document
public class UserGroup extends DomainResource {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3896371055741103341L;
	@DBRef
	@NotNull
	private AppUser user;
	@DBRef
	@NotNull
	private Group group;
}
