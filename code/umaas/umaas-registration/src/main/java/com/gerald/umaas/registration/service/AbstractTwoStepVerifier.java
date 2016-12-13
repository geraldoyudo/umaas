package com.gerald.umaas.registration.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.gerald.umaas.registration.entities.Token;
import com.gerald.umaas.registration.managers.TokenManager;


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
		Token token = 
				tokenManager.createToken(name, code, "verification" ,
						(Map<String,Object>)request.get("tokenValues"));
		request.set("code", code);
		request.set("tokenId", token.getId());
		doRequest(request);
		return  token.getId();
	}
	
    protected String generateRandomCode(){
    	return generator.generateCode();
    }
    
    @Override
    protected final Map<String,Object> onProcess(VerificationRequest request) {
    	String code = request.get("code").toString();
    	String tokenId = request.get("tokenId").toString();
    	Token token = tokenManager.get(tokenId);
    	if(token == null)
    		return returnValue(false);
    	if(!token.getCode().equals(code))
    		return returnValue(false);
    	tokenManager.delete(token);
    	request.set("tokenValues", token.getProperties());
    	return doProcess(request);
    }
    
    protected Map<String,Object> doProcess(VerificationRequest request){
    	return returnValue(true, (Map<String,Object>)request.get("tokenValues"));
    }
    
    @Override
    protected String onResend(VerificationRequest request) {
    	String tokenId = request.get("tokenId").toString();
    	Token t = tokenManager.get(tokenId);
    	if(t != null){
    		tokenManager.delete(t);
    	}
    	Token newToken = tokenManager
    			.createToken(t.getEntityType(), generateRandomCode()
    			, t.getPurpose(),(Map<String,Object>)request.get("tokenValues"));
    	request.set("code", newToken.getCode());
		request.set("tokenId", newToken.getId());
		doRequest(request);
    	return newToken.getId();
    }
    
    @Override
    public VerifierType getType() {
    	return VerifierType.TWO_STEP;
    }
}
