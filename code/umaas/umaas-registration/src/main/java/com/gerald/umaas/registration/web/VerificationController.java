package com.gerald.umaas.registration.web;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.gerald.umaas.registration.service.VerificationRequest;
import com.gerald.umaas.registration.service.VerifierNotSupportedException;

@Controller
public class VerificationController {
	@Autowired
	private VerificationAttendant verificationAttendant;
	@Value("${app.hostUrl}")
	private String hostUrl;
	
	@RequestMapping(path= "/verify/process", method = RequestMethod.GET)
	public String processVerification(
			@RequestParam("name") String name,
			@RequestParam("value") String value,
			@RequestParam("code") String code,
			@RequestParam("tokenId") String tokenId, Model model) throws VerifierNotSupportedException{
		VerificationRequest req = new VerificationRequest();
		req.setName(name);
		req.setValue(value);
		req.set("code", code);
		req.set("tokenId", tokenId);
		model.addAttribute("request", req);
		Map<String,Object> result =  verificationAttendant.processVerification(req);
		model.addAttribute("result", result);
		return "verification-result";
	}

}
