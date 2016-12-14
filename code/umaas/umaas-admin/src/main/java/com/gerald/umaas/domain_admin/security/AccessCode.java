package com.gerald.umaas.domain_admin.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Data;

@Data
public class AccessCode implements UserDetails{

	private String id;
	private String code;
	private Date expiryDate;
	private boolean notExpired = true;
	private boolean enabled = true;
	private boolean notLocked = true;
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return new ArrayList<>();
	}

	@Override
	public String getPassword() {
		return getCode();
	}

	@Override
	public String getUsername() {
		return getId();
	}

	@Override
	public boolean isAccountNonExpired() {
		if(expiryDate == null)
			return true;
		else{
			return expiryDate.before(new Date());
		}
	}

	@Override
	public boolean isAccountNonLocked() {
		return notLocked;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return notExpired;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}


}
