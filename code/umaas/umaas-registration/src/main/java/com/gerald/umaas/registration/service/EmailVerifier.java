package com.gerald.umaas.registration.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.gerald.utils.communication.AsyncSender;
import com.gerald.utils.communication.CustomFormatMimeEmailSender;

@Component
public class EmailVerifier  extends AbstractTwoStepVerifier{
	@Autowired
	@Qualifier("customFormatMimeMailSender")
	private AsyncSender<Map<String,Object>> htmlMailSender;
	@Value("${app.host:http://localhost:8071}")
	private String hostUrl;
	
	
	@Override
	public void doRequest(VerificationRequest request) {
		Map<String, Object> values = new HashMap<>();
		values.put("request", request);
		values.put(CustomFormatMimeEmailSender.TO, request.getValue());
		String verifyUrlTemplate = hostUrl + "/app/verify/process?name=%s&code=%s&value=%s&tokenId=%s";
		String verifyUrl = String.format(verifyUrlTemplate, 
				request.getName(), request.get("code"), request.getValue(), request.get("tokenId"));
		values.put("verifyUrl", verifyUrl);
		System.out.println(verifyUrl);

		htmlMailSender.sendAsync(values);
	
		
	}

	@Override
	protected boolean supports(VerificationRequest request) {
		return request.getName().equals("email");
	}

}
