/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gerald.umaas.domain.repositories;


import com.gerald.umaas.domain.entities.ServiceConfiguration;
import com.gerald.umaas.domain.entities.ServiceConfiguration.PluginType;

/**
 *
 * @author Dev7
 */
public interface PluginConfigurationRepository extends DomainResourceRepository<ServiceConfiguration, String>{
    public ServiceConfiguration findByPluginIdAndTypeAndDomainId(String pluginId, PluginType type, String domainId);    
}
