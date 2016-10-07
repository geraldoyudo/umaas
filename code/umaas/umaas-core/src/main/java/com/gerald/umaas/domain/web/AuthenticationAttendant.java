package com.gerald.umaas.domain.web;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
