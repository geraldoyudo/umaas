package com.gerald.umaas.registration.service;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VerifierObject implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4577530362721944151L;
	private VerifierType type;
	private String name;
}
