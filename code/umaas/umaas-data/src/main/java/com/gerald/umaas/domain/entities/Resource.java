/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gerald.umaas.domain.entities;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 * @author Dev7
 */

@Data
@EqualsAndHashCode(of = "id")
public abstract class Resource implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1682701690404504546L;
	@Id
    private String id;
    @Indexed
    @NotNull
    private String externalId;
    private Map<String, Object> meta = new HashMap<>();
    public Resource() {
        externalId = UUID.randomUUID().toString();
    }
    public void meta(String key, Object value){
        meta.put(key, value);
    }
    
    public Object meta(String key){
        return meta.get(key);
    }
}
