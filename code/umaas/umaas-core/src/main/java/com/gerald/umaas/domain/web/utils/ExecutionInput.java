package com.gerald.umaas.domain.web.utils;

import java.util.Map;

import lombok.Data;

@Data
public  class ExecutionInput{
	private String method;
	private Map<String, Object> input;
}