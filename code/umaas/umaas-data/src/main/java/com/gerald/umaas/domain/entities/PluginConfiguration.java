package com.gerald.umaas.domain.entities;

public class PluginConfiguration extends Resource {
	public enum PluginType {DOMAIN, SYSTEM};
	
	private String pluginId;
	private PluginType type;
	private String domainId;
	private boolean enabled;
}
