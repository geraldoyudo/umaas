angular.module('app')

.service('customServiceAttendant', function($q, umaas, $http, globalConfig){
	var domainId = umaas.getDomain().id;
	var self = this;
	var baseUrl = globalConfig.basePath + '/umaas/core';
	
	var checkNull = function(variable){
		if(!variable) throw "Null data present";
	}
	self.getServices = function(){
		return $http.get(baseUrl + '/endpoint', function(resp){
			return resp.data.data;
		})
	}
	self.getConfigurationSpecs = function(serviceId){
		checkNull(serviceId);
		return $http.get(baseUrl + '/endpoint/' + serviceId + '/configure', function(resp){
			return resp.data.data;
		});
	}
	
	self.getConfiguration = function(serviceId, domainId){
		checkNull(serviceId);
		checkNull(domainId);
		return $http.get(baseUrl + '/endpoint/' + serviceId + '/' + domainId + '/properties', function(resp){
			return resp.data.data;
		});
	}
	
	self.saveConfiguration = function(serviceId, domainId,configuration){
		checkNull(serviceId);
		checkNull(domainId);
		checkNull(configuration);
		return $http.post(baseUrl + '/endpoint/' +
				serviceId + '/' + domainId + '/properties', configuration, function(resp){
			return resp.data.data;
		});
	}
	
	self.execute = function(serviceId, method, input){
		var executionInput = {method: method, input: input};
		var deferred = $q.defer();
		 $http.post(baseUrl + '/endpoint/' +
				serviceId + '/' + domainId + '/execute', executionInput, function(resp){
			deferred.resolve(resp.data.value);
		});
		 return deferred.promise;
	}
})

