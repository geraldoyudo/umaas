package com.gerald.umaas.extensionpoint;

public interface ServiceUI {
	public String getId();
	public String getName();
	public String getDescription();
	public String getTemplate();
	public void setEnabled(String domainId, boolean enabled);
	public boolean isEnabled(String domainId);
}
