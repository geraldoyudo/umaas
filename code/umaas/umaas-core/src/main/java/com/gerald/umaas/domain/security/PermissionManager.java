package com.gerald.umaas.domain.security;

import com.gerald.umaas.domain.entities.DomainAccessCodeMapping.Priviledge;

public interface PermissionManager {
	public boolean hasPermission(String entityType, String entityId, Priviledge priviledge);
	public boolean hasDomainCollectionPermission(String domainId, String entityType, Priviledge priviledge);
	public boolean hasPermissionWithExternalId(String domainId, String entityType, String entityExternalId, Priviledge priviledge);
	public boolean hasUserDomainPermission(String entityType, String userId, Priviledge priviledge);
	public boolean hasAffiliateDomainPermission(String entityType, String key, Priviledge priviledge);

}
