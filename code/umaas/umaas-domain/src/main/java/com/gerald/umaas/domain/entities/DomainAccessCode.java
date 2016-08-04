package com.gerald.umaas.domain.entities;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.index.Indexed;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DomainAccessCode extends Resource{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1136783221457394596L;
	@Indexed(unique = true)
	private String value;
	private LocalDateTime expiryDate;

}
