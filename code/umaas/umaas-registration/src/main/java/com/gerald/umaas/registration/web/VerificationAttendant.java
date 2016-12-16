package com.gerald.umaas.registration.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.gerald.umaas.registration.service.VerificationRequest;
import com.gerald.umaas.registration.service.Verifier;
import com.gerald.umaas.registration.service.VerifierNotSupportedException;

@RestController
public class VerificationAttendant {
	@Autowired
	@Qualifier("generalVerifier")
	private Verifier generalVerifier;
	@Autowired
	private HttpSession session;
	
	@RequestMapping(path= "/verify/request", method = RequestMethod.POST)
	public Map<String,String> requestVerification(@RequestBody 
			VerificationRequest request) throws VerifierNotSupportedException{
		System.out.println(request);
		session.setAttribute("domainName", request.get("domain"));
		String id = generalVerifier.request(request);
		Map<String,String> map = new HashMap<>();
		map.put("id", id);
		return map;
	}
	
	@RequestMapping(path= "/verify/resend", method = RequestMethod.POST)
	public Map<String,String> resend(@RequestBody 
			VerificationRequest request) throws VerifierNotSupportedException{
		System.out.println(request);
		session.setAttribute("domainName", request.get("domain"));
		String id = generalVerifier.resend(request);
		Map<String,String> map = new HashMap<>();
		map.put("id", id);
		return map;
	}
	
	@RequestMapping(path= "/verify/process", method = RequestMethod.POST)
	public Map<String,Object> processVerification(@RequestBody 
			VerificationRequest request) throws VerifierNotSupportedException{
		return generalVerifier.process(request);
	}
	
	
}
