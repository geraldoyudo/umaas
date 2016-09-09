package com.gerald.umaas.registration.web;

import java.util.HashMap;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.gerald.umaas.registration.service.VerifierNotSupportedException;

@ControllerAdvice
public class ExceptionResolver {
	@ExceptionHandler(NullPointerException.class)
	public ResponseEntity<HashMap<String,String>> notFoundHandler(Exception ex){
		ex.printStackTrace();
		HashMap<String,String> result = new HashMap<>();
		result.put("message" , ex.getMessage());
		result.put("description", "Not found");
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
	}
	@ExceptionHandler( IllegalArgumentException.class)
	public ResponseEntity<HashMap<String,String>> handleIllegalArgumentException(Exception ex){
		HashMap<String,String> values = new HashMap<>();
		values.put("message", ex.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(values);
	}
	
	@ExceptionHandler(VerifierNotSupportedException.class)
	public ResponseEntity<HashMap<String,String>> handleUnsupportedPolicies(){
		HashMap<String,String> values = new HashMap<>();
		values.put("message", "Verifier is not supported");
		return ResponseEntity.badRequest().body(values);
	}
	
	
}
