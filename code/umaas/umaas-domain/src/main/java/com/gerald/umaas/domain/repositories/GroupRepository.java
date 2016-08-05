/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gerald.umaas.domain.repositories;

import com.gerald.umaas.domain.entities.Group;
import com.gerald.umaas.domain.entities.Domain;
import java.util.List;

/**
 *
 * @author Dev7
 */
public interface GroupRepository extends DomainResourceRepository<Group, String>{
    public List<Group> findByDomain(Domain domain);
    public Group findByDomainAndName(Domain domain, String name);
}
