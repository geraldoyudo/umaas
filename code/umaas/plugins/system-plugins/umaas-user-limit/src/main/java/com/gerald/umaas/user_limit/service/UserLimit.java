package com.gerald.umaas.user_limit.service;

import org.springframework.data.annotation.Transient;

import lombok.Data;

@Data
public class UserLimit {
	private long limit = 100;
	@Transient
	private long size = 0;
}
