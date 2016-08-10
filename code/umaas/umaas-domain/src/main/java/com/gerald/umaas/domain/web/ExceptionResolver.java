package com.gerald.umaas.domain.web;

import java.util.HashMap;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

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

	@ExceptionHandler(Exception.class)
	public ResponseEntity<HashMap<String,String>> handleException(Exception ex){
		HashMap<String,String> values = new HashMap<>();
		values.put("message", ex.getMessage());
		ex.printStackTrace();
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(values);
	}
	

	@ExceptionHandler(value = {ClassCastException.class, IllegalArgumentException.class})
	public ResponseEntity<HashMap<String,String>> handleClassCastException(Exception ex){
		HashMap<String,String> values = new HashMap<>();
		values.put("message", ex.getMessage());
		return ResponseEntity.badRequest().body(values);
	}
	
	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<HashMap<String,String>> handleDataIntegrityViolation(){
		HashMap<String,String> values = new HashMap<>();
		values.put("message", "A record with a unique key specified already exists");
		values.put("error", "Data Integrity Violation");
		return ResponseEntity.badRequest().body(values);
	}
	
}
