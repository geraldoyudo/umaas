/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gerald.umaas.domain.entities;

import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.NotNull;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 * @author Dev7
 */
@Document
@CompoundIndexes({
    @CompoundIndex(name = "domain_key_type", def = "{'domain.$id' : 1, 'key' : 1, 'type' : 1}", unique = true)
})
@Data
@EqualsAndHashCode(callSuper=true)
public class RoleMapping extends DomainResource{
    /**
	 * 
	 */
	private static final long serialVersionUID = 8817921024593304596L;

	public static enum RoleMappingType {GROUP, USER};
    @NotNull
    private RoleMappingType type = RoleMappingType.USER;
    @NotNull
    private String key;
    @DBRef
    private Set<Role> roles = new HashSet<>();

    public void removeRole(Role r){
        this.roles.remove(r);
    }
    
    public void addRole(Role r){
        this.roles.add(r);
    }
    
    
}
