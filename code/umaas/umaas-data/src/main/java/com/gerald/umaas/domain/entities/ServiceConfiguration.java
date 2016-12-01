package com.gerald.umaas.domain.entities;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper= true)
@Document
@CompoundIndexes({
    @CompoundIndex(name = "plugin_type_domain", def = "{'pluginId' : 1, 'type' : 1,'domain.$id' : 1}", unique = true)
})
@NoArgsConstructor
@AllArgsConstructor
public class ServiceConfiguration extends DomainResource {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2072328833117493925L;
	public enum PluginType {DOMAIN, SYSTEM};
	
	private String pluginId;
	private PluginType type = PluginType.SYSTEM;
	private boolean enabled = true;
	private Map<String,Object> configuration = new HashMap<>();
	public ServiceConfiguration(String pluginId, PluginType type,Domain d, boolean enabled, Map<String, Object> configuration) {
		this(pluginId, type, enabled, configuration);
		setDomain(d);
	}
	
}
