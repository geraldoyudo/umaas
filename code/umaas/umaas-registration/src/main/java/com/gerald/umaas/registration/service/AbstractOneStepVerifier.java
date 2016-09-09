package com.gerald.umaas.registration.service;

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
	protected final boolean onProcess(VerificationRequest request) {
		return doProcess(request);
	}
	
	protected abstract boolean doProcess(VerificationRequest request);
}
