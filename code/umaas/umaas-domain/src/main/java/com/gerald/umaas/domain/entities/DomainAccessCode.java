package com.gerald.umaas.domain.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DomainAccessCode extends Resource implements UserDetails{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1136783221457394596L;
	@Indexed(unique = true)
	private String code;
	private LocalDateTime expiryDate;
	private boolean notExpired = true;
	private boolean notLocked = true;
	private boolean enabled = true;
	@Transient
	private List<String> accessRoles = new ArrayList<>();
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<SimpleGrantedAuthority> authorities = new ArrayList<>();
		for(String role: accessRoles){
			authorities.add(new SimpleGrantedAuthority("Role_" + role));
		}
		return authorities;
	}
	@Override
	public String getPassword() {
		return code;
	}
	@Override
	public String getUsername() {
		return getId();
	}
	@Override
	public boolean isAccountNonExpired() {
		return expiryDate.isAfter(LocalDateTime.now());
	}
	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean isCredentialsNonExpired() {
		return  notExpired;
	}
	@Override
	public boolean isEnabled() {
		return enabled;
	}

}
