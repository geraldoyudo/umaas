package com.gerald.umaas.domain.entities;

import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
@CompoundIndexes({
    @CompoundIndex(name = "domain_name", def = "{'domain.$id' : 1, 'name' : 1}", unique = true)
})
public class Field extends DomainResource{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2489409244831566548L;
	@NotNull
    @Indexed
    private String name;
    private boolean mandatory = true;
    private boolean registrationItem = true;
    @NotNull
	private String type = "string";
    private Map<String,Object> properties = new HashMap<>();
}
