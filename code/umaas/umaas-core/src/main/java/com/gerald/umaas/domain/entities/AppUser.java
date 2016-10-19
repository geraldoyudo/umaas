package com.gerald.umaas.domain.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.gerald.umaas.domain.entities.RoleMapping.RoleMappingType;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Document
@CompoundIndexes({
    @CompoundIndex(name = "domain_username", def = "{'domain.$id' : 1, 'username' : 1}", unique = true),
    @CompoundIndex(name = "domain_email", def = "{'domain.$id' : 1, 'email' : 1}", unique = true)
})
public class AppUser extends DomainResource implements Affiliate{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8619746011519219776L;
	@NotNull
    @Indexed
    private String username;
    @NotNull
    private String password;
    @NotNull
    private String email;
    @NotNull
    private String phoneNumber;
    private boolean emailVerified;
    private boolean phoneNumberVerified;
    private boolean disabled;
    private boolean locked;
    private boolean credentialsExpired;
    private Map<String,Object> properties = new HashMap<>();
    private List<String> groups = new ArrayList<>();
    private List<String> roles = new ArrayList<>();
	@Override
	public String key() {
		return getId();
	}
	@Override
	public RoleMappingType type() {
		return RoleMappingType.USER;
	}
	
	public void setProperties(Map<String,Object> properties){
		if (properties == null){
			return;
		}
		this.properties = properties;
	}
	public void setGroups(List<String> groups){
		if(groups == null){
			return;
		}
		this.groups = groups;
	}
	public void setRoles(List<String> roles){
		if(roles == null){
			return;
		}
		this.roles = roles;
	}

}
