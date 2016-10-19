package com.gerald.umaas.domain.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gerald.umaas.domain.entities.AppUser;
import com.gerald.umaas.domain.repositories.UserRepository;

@RestController
public class AuthenticationAttendant {
	@Autowired
    private UserRepository userRepository;
	
  
    
    @RequestMapping(path = "/domain/auth/authenticate", method = RequestMethod.GET, produces = "application/json")
     public Map<String,Object> authenticate(@RequestParam("user") String username, 
             @RequestParam("password") String password, @RequestParam("domain") String domain){
        AppUser appUser = userRepository.findByUsernameAndDomain(username, domain);
        Map<String,Object> ret = new HashMap<>();
        ret.put("auth", false);
         if(appUser == null) return ret;
         
         if(appUser.isCredentialsExpired() || appUser.isDisabled() || 
        		 appUser.isLocked()){
        	 List<String> messages = new ArrayList<>();
        	  if(appUser.isCredentialsExpired()){
             	 messages.add("credentials expired");
              }
        	  if(appUser.isDisabled()){
        		  messages.add("Account is disabled");
        	  }
        	  if(appUser.isLocked()){
        		  messages.add("Account is locked");
        	  }
        	  ret.put("messages", messages);
        	  return ret;
         }
       
         String encryptedPassword = encrypt(password);
         if(!appUser.getPassword().equals(encryptedPassword))
        	 return ret;
         ret.put("auth", true);
         ret.put("roles", appUser.getRoles());
         return ret;
     }
     private String encrypt(String str){
         return str;
     }
}
