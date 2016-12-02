package com.gerald.umaas.file_limit.web;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.gerald.umaas.file_limit.service.DomainLimitService;
import com.gerald.umaas.file_limit.service.FileLimit;

@RestController
public class FileLimitController {
	private DomainLimitService domainLimitService;
	
	public FileLimitController(DomainLimitService domainLimitService){
		this.domainLimitService = domainLimitService;
	}
	
	@RequestMapping(path = "/fileLimit/approveSave", method = RequestMethod.POST)
	public boolean approveUpdate(@RequestBody FileEntry entry){
		if(!domainLimitService.isEnabled())return true;
		String[] pathChunks = entry.getDirectory().split("/");
		if(pathChunks.length == 0){
			throw new IllegalArgumentException("Could not extract domain Id");
		}
		String domainId =pathChunks[0];
		
		FileLimit limit = domainLimitService.getLimitByDomain(domainId);
		long increasedSize = limit.getSize() - entry.getOldSize() + entry.getNewSize();
		if(increasedSize > limit.getLimit()){
			return false;
		}else{
			limit.setSize(increasedSize);
			domainLimitService.saveLimitByDomain(domainId, limit);
			return true;
		}
	}

}
