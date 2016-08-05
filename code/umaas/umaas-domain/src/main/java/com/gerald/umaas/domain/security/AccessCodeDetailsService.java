package com.gerald.umaas.domain.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.gerald.umaas.domain.repositories.DomainAccessCodeRepository;

@Component
public class AccessCodeDetailsService implements UserDetailsService{
	@Autowired
	private DomainAccessCodeRepository accessCodeRepository;
	
	
	
	@Override
	public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
		return accessCodeRepository.findOne(id);
	}

}
