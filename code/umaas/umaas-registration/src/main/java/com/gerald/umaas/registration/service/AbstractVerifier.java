package com.gerald.umaas.registration.service;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractVerifier implements Verifier{
	protected abstract boolean supports(VerificationRequest request);
	protected abstract Map<String,Object> onProcess(VerificationRequest request);	
	protected abstract String onRequest(VerificationRequest request);
	protected abstract String onResend(VerificationRequest request);
	
	@Override
	public final Map<String,Object> process(VerificationRequest request) throws VerifierNotSupportedException {
		if(!supports(request))
			throw new VerifierNotSupportedException();
		return onProcess(request);	
	}
	
	@Override
	public final String request(VerificationRequest request) throws VerifierNotSupportedException {
		if(!supports(request))
			throw new VerifierNotSupportedException();
		return onRequest(request);	
	}
	
	@Override
	public String resend(VerificationRequest request) throws VerifierNotSupportedException {
		if(!supports(request))
			throw new VerifierNotSupportedException();
		return onResend(request);	
	}
	
	protected static Map<String,Object> returnValue(boolean okay){
		HashMap<String,Object> ret = new HashMap<>();
		ret.put("verified", okay);
		return ret;
	}
	protected static Map<String,Object> returnValue(boolean okay, Map<String,Object> properties){
		HashMap<String,Object> ret = new HashMap<>();
		ret.put("verified", okay);
		ret.put("values", properties);
		return ret;
	}
}
