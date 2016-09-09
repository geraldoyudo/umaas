package com.gerald.umaas.registration.service;

public abstract class AbstractVerifier implements Verifier{
	protected abstract boolean supports(VerificationRequest request);
	protected abstract boolean onProcess(VerificationRequest request);	
	protected abstract String onRequest(VerificationRequest request);
	@Override
	public final boolean process(VerificationRequest request) throws VerifierNotSupportedException {
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
	
}
