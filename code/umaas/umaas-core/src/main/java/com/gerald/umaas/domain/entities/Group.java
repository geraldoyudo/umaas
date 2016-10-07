package com.gerald.umaas.domain.entities;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.gerald.umaas.domain.entities.RoleMapping.RoleMappingType;

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
public class Group extends DomainResource implements Affiliate{
	  /**
	 * 
	 */
	private static final long serialVersionUID = -7736290188100805420L;
	@NotNull
	@Indexed
	private String name;
	@DBRef
	private Group parent = null;
	@Transient
	private List<String> roles;
	
	@Override
	public String key() {
		return getId();
	}
	@Override
	public RoleMappingType type() {
		return RoleMappingType.GROUP;
	}
}
