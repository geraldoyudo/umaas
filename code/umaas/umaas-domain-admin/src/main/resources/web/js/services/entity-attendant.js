angular.module('app')

.service('entityAttendant', function($q, DomainConstants, umaas){
	var creatorMap = {};
	creatorMap[DomainConstants.Entity.User] = umaas.AppUser;
	creatorMap[DomainConstants.Entity.Group] = umaas.Group;
	creatorMap[DomainConstants.Entity.Role] = umaas.Role;
	creatorMap[DomainConstants.Entity.Field] = umaas.Field;
	
	var loadMap = {};
	loadMap[DomainConstants.Entity.User] = umaas.appUsers;
	loadMap[DomainConstants.Entity.Group] = umaas.groups;
	loadMap[DomainConstants.Entity.Role] = umaas.roles;
	loadMap[DomainConstants.Entity.Field] = umaas.fields;
	
	
	this.getEntities = function(entityName, paginationData){
		var deferred = $q.defer();
		if(!loadMap[entityName]) deferred.reject();
		loadMap[entityName].find(paginationData, function(error,entities){
			if(error) deferred.reject(error);
			deferred.resolve(entities);
		})
		return deferred.promise;
	}
	
	this.newEntity = function(entityType){
		if(!creatorMap[entityName]) return;
		return new creatorMap[entityName]();
	}
})