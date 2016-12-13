package com.gerald.umaas.registration.service;

import java.util.Map;

public abstract class AbstractOneStepVerifier extends AbstractVerifier{
	@Override
	public final String onRequest(VerificationRequest request) {
		return null;
	}
	
	@Override
	protected String onResend(VerificationRequest request) {
		return null;
	}
	@Override
	protected final Map<String,Object> onProcess(VerificationRequest request) {
		return doProcess(request);
	}
	
	protected abstract Map<String,Object> doProcess(VerificationRequest request);
	
	@Override
	public VerifierType getType() {
		return VerifierType.ONE_STEP;
	}
}
