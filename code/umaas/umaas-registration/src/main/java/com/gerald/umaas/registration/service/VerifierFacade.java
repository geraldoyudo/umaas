package com.gerald.umaas.registration.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("generalVerifier")
public class VerifierFacade implements Verifier{
	@Autowired(required = false)
	private List<AbstractVerifier> verifiers;
	
	@Override
	public boolean process(VerificationRequest request) throws VerifierNotSupportedException {
		if(verifiers == null) throw new VerifierNotSupportedException();
		for(Verifier verifier: verifiers){
			try{
				return verifier.process(request);
			}catch(VerifierNotSupportedException ex){
				continue;
			}		
		}
		throw new VerifierNotSupportedException();
	}

	@Override
	public String request(VerificationRequest request) throws VerifierNotSupportedException {
		if(verifiers == null) throw new VerifierNotSupportedException();
		for(Verifier verifier: verifiers){
			try{
				return verifier.request(request);
			}catch(VerifierNotSupportedException ex){
				continue;
			}		
		}
		throw new VerifierNotSupportedException();
	}
	@Override
	public String resend(VerificationRequest request) throws VerifierNotSupportedException {
		if(verifiers == null) throw new VerifierNotSupportedException();
		for(Verifier verifier: verifiers){
			try{
				return verifier.resend(request);
			}catch(VerifierNotSupportedException ex){
				continue;
			}		
		}
		throw new VerifierNotSupportedException();
	}
}
