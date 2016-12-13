package com.gerald.umaas.registration.managers;

import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.gerald.umaas.registration.entities.Token;
import com.gerald.umaas.registration.repositories.TokenRepository;

@Component
public class TokenManager{
	@Value("${token.TTL}")
	private long timeToLive = 3*1000;
	 
	@Autowired 
	private TokenRepository tokenRepository;
	    
	
    public Token createToken(String entityType, String code, String purpose) {
    	 Token t = new Token();
    	 t.setCode(code);
    	 t.setEntityType(entityType);
    	 t.setExpiryDate(new Date(System.currentTimeMillis() + timeToLive));
    	 t.setPurpose(purpose);
    	 return tokenRepository.save(t);
    }
    
    public Token createToken(String entityType, String code, String purpose, Map<String, Object> properties) {
    	 Token t = new Token();
    	 t.setCode(code);
    	 t.setEntityType(entityType);
    	 t.setExpiryDate(new Date(System.currentTimeMillis() + timeToLive));
    	 t.setPurpose(purpose);
    	 t.setProperties(properties);
		return tokenRepository.save(t);
	}
    public Token get(String id){
    	return tokenRepository.findOne(id);
    }
	
    @Scheduled(initialDelayString = "${tokenManager.InitialDelay}", 
            fixedRateString = "${tokenManager.DeleteCycle}")
    public void removedExpiredTokens() {
    	System.out.println("Removing expired tokens");
        tokenRepository.deleteByExpiryDateLessThan(new Date(System.currentTimeMillis()));
    }

  
    public void removeToken(Token t) {
    	tokenRepository.delete(t);
    }
    
    public long getTimeToLive() {
        return timeToLive;
    }

    public void setTimeToLive(long timeToLive) {
        this.timeToLive = timeToLive;
    }

	public void delete(Token token) {
		tokenRepository.delete(token);
	}

	
	    
	   
}
