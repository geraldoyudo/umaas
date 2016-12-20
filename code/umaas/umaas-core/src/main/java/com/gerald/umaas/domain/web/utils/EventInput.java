package com.gerald.umaas.domain.web.utils;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;

@Data
public class EventInput {
	private String type;
	private Map<String,Object> data = new HashMap<>();
}
