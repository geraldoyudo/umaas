package com.gerald.umaas.registration.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.gerald.utils.communication.AsyncSender;
import com.gerald.utils.communication.CustomFormatMimeEmailSender;
import com.gerald.utils.communication.ThymeleafMimeEmailConstructor;

@Component
public class PasswordResetVerifier  extends AbstractTwoStepVerifier{
	@Autowired
	@Qualifier("customFormatMimeMailSender")
	private AsyncSender<Map<String,Object>> htmlMailSender;
	@Value("${app.host:http://localhost:8071}")
	private String hostUrl;
	@Value("${app.emailVerifier.subject:Password Reset}")
	private String passwordRegistrationSubject;
	
	@Override
	public void doRequest(VerificationRequest request) {
		Map<String, Object> values = new HashMap<>();
		values.put("request", request);
		values.put(CustomFormatMimeEmailSender.TO, request.getValue());
		Object subject = request.get("subject");
		if(subject == null){
			subject =  passwordRegistrationSubject;
		}
		values.put(CustomFormatMimeEmailSender.SUBJECT, subject.toString());
		values.put(ThymeleafMimeEmailConstructor.HTML_TEMPLATE
				, "password_reset.html");
		String verifyUrlTemplate = hostUrl + "/app/passwordReset?domain=%s#/passwordReset/changePassword/?code=%s&tokenId=%s";
		String verifyUrl = String.format(verifyUrlTemplate, request.get("domain"),
				request.get("code"), request.get("tokenId"));
		values.put("verifyUrl", verifyUrl);
		System.out.println(verifyUrl);
		htmlMailSender.sendAsync(values);	
	}

	@Override
	protected boolean supports(VerificationRequest request) {
		return request.getName().equals("password-reset");
	}

}
