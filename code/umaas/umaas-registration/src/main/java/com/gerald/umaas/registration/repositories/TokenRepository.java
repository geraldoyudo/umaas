package com.gerald.umaas.registration.repositories;

import java.util.Date;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.transaction.annotation.Transactional;

import com.gerald.umaas.registration.entities.Token;


@Transactional
public interface TokenRepository
extends MongoRepository<Token, String>{
	  public void deleteByExpiryDateLessThan(Date date);
}
