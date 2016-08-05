package com.gerald.umaas.domain.security;

import com.gerald.umaas.domain.entities.DomainAccessCodeMapping.Priviledge;

public interface PermissionManager {
	public boolean hasPermission(String entityType, String entityId, Priviledge priviledge);
}
