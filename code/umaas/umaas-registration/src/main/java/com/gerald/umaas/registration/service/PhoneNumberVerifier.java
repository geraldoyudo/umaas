package com.gerald.umaas.registration.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;

import com.gerald.utils.communication.AsyncSender;
import com.gerald.utils.communication.entities.SMSMessage;

@Component
public class PhoneNumberVerifier extends AbstractTwoStepVerifier {
	@Autowired
	@Qualifier("asyncSmsSender")
	private AsyncSender<SMSMessage> smsSender;
	@Autowired
	@Qualifier("appMessageService")
	private MessageSourceAccessor messageSource;
	@Value("${app.sms.origin:umaas}")
	private String smsOrigin;
	
	@Override
	public void doRequest(VerificationRequest request) {
	 SMSMessage m = new SMSMessage(request.getValue().toString(), 
				smsOrigin, messageSource.getMessage(
						"alert.verification.request", new Object[]{request.get("code")}));
		smsSender.sendAsync(m);
		
	}

	@Override
	protected boolean supports(VerificationRequest request) {
		return request.getName().equals("phone");
	}
	

}
