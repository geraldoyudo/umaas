package com.gerald.umaas.registration.service;

import java.util.Map;

public interface Verifier {
	public Map<String,Object> process(VerificationRequest request) throws VerifierNotSupportedException;
	public String request(VerificationRequest request) throws VerifierNotSupportedException;
	public String resend(VerificationRequest request) throws VerifierNotSupportedException;
	public VerifierType getType();
}
