package com.gerald.umaas.registration.service;

public interface Verifier {
	public boolean process(VerificationRequest request) throws VerifierNotSupportedException;
	public String request(VerificationRequest request) throws VerifierNotSupportedException;
}
