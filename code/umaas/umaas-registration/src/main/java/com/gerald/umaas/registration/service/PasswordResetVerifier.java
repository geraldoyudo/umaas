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
	@Override
	public void doRequest(VerificationRequest request) {
		Map<String, Object> values = new HashMap<>();
		values.put("request", request);
		values.put(CustomFormatMimeEmailSender.TO, request.getValue());
		values.put(CustomFormatMimeEmailSender.SUBJECT, "Password Reset");
		values.put(ThymeleafMimeEmailConstructor.HTML_TEMPLATE
				, "password_reset.html");
		htmlMailSender.sendAsync(values);	
	}

	@Override
	protected boolean supports(VerificationRequest request) {
		return request.getName().equals("password-reset");
	}

}
