package com.gerald.umaas.domain.entities;

import javax.validation.constraints.NotNull;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.DBRef;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@CompoundIndexes({
    @CompoundIndex(name = "code_domain_aspect_priviledge", 
    		def = "{'accessCode.$id' : 1, 'domain.$id' : 1, "
    				+ "'aspect' : 1, 'priviledge' : 1}", 
    				unique = true)
})
public class DomainAccessCodeMapping extends Resource{
	/**
	 * 
	 */
	private static final long serialVersionUID = 9167212043221438461L;
	@DBRef
	@NotNull
	private DomainAccessCode accessCode;
	@DBRef
	private Domain domain;
	private Aspect aspect;
	private Priviledge priviledge;
	
	public enum Aspect {
		ALL,
		FIELD,
		USER,
		ROLE,
		GROUP,
		ADMIN
	}
	public enum Priviledge {
		ALL,
		VIEW,
		UPDATE,
		DELETE,
		ADD
	}
}
