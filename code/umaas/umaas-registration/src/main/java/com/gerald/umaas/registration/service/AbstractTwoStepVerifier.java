package com.gerald.umaas.registration.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gerald.umaas.registration.entities.Token;
import com.gerald.umaas.registration.managers.TokenManager;

@Component
public abstract class AbstractTwoStepVerifier extends AbstractVerifier {
	@Autowired
	private TokenManager tokenManager;
	@Autowired
	private RandomCodeStringGenerator generator;
	
	public abstract void doRequest(VerificationRequest request);
	
	
	@Override
	public final String onRequest(VerificationRequest request) {
		String name = request.getName();
		String code = generateRandomCode();
		Token token = tokenManager.createToken(name, code, "verification");
		doRequest(request);
		return  token.getId();
	}
	
    protected String generateRandomCode(){
    	return generator.generateCode();
    }
    
    @Override
    protected final boolean onProcess(VerificationRequest request) {
    	String code = request.get("code").toString();
    	String tokenId = request.get("tokenId").toString();
    	Token token = tokenManager.get(tokenId);
    	if(token == null)
    		return false;
    	if(!token.getCode().equals(code))
    		return false;
    	return doProcess(request);
    }
    
    protected boolean doProcess(VerificationRequest request){
    	return true;
    }
}
