package com.gerald.umaas.user_limit.web;

import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

import com.gerald.umaas.domain.entities.AppUser;
import com.gerald.umaas.domain.entities.Domain;
import com.gerald.umaas.user_limit.service.UserLimit;
import com.gerald.umaas.user_limit.service.UserLimitService;

@Component
@RepositoryEventHandler
public class UserLimitEnforcer {
	private UserLimitService domainLimitService;
	
	public UserLimitEnforcer(UserLimitService domainLimitService){
		this.domainLimitService = domainLimitService;
	}
	
	@HandleBeforeCreate
	public void approveUpdate(AppUser user){
		if(!domainLimitService.isEnabled())return;
		Domain d = user.getDomain();
		if(d == null) return;
		UserLimit userLimit = domainLimitService.getLimitByDomain(d.getId());
		long newSize = userLimit.getSize() + 1;
		if(newSize > userLimit.getLimit()){
			throw new IllegalStateException("User Limit Exceeded");
		}
	}

}
