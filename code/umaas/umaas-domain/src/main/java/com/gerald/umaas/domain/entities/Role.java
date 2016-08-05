package com.gerald.umaas.domain.entities;

import javax.validation.constraints.NotNull;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@Document
@CompoundIndexes({
    @CompoundIndex(name = "domain_name", def = "{'domain.$id' : 1, 'name' : 1}", unique = true)
})
@Data
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Role extends DomainResource{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2376250137792268095L;
	@NotNull
	@Indexed
	private String name;
}
