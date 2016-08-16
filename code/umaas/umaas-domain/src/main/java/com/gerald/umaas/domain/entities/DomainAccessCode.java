package com.gerald.umaas.domain.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

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

	@JsonIgnore
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<SimpleGrantedAuthority> authorities = new ArrayList<>();
		return authorities;
	}
	@JsonIgnore
	@Override
	public String getPassword() {
		return code;
	}
	@JsonIgnore
	@Override
	public String getUsername() {
		return getId();
	}
	@JsonIgnore
	@Override
	public boolean isAccountNonExpired() {
		if(expiryDate == null)
			return true;
		else
			return expiryDate.isAfter(LocalDateTime.now());
	}
	@JsonIgnore
	@Override
	public boolean isAccountNonLocked() {
		return notLocked;
	}
	@JsonIgnore
	@Override
	public boolean isCredentialsNonExpired() {
		return  notExpired;
	}
	@Override
	public boolean isEnabled() {
		return enabled;
	}

}
