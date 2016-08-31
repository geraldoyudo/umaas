package com.gerald.umaas.domain.entities;

import javax.validation.constraints.NotNull;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@CompoundIndexes({
    @CompoundIndex(name = "code_type_id_priviledge", 
    		def = "{'accessCode.$id' : 1, 'entityType' : 1, "
    				+ "'entityId' : 1, 'priviledge' : 1}", 
    				unique = true)
})
@Document
public class DomainAccessCodeMapping extends Resource{
	/**
	 * 
	 */
	private static final long serialVersionUID = 9167212043221438461L;
	@DBRef
	@NotNull
	private DomainAccessCode accessCode;
	private String entityType;
	private String entityId;
	private Priviledge priviledge;
	private boolean enforced = true;
	
	public enum Priviledge {
		ALL,
		VIEW,
		UPDATE,
		DELETE,
		ADD,
		NONE
	}
}
