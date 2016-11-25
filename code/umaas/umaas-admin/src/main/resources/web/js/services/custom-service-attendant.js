angular.module('app')

.service('customServiceAttendant', function($q, umaas, $http){
	var domainId = umaas.getDomain().id;
	var self = this;
	var baseUrl = umaas.getBaseUrl();
	
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
	
	self.getConfiguration = function(serviceId){
		checkNull(serviceId);
		checkNull(domainId);
		return $http.get(baseUrl + '/endpoint/' + serviceId + '/' + domainId + '/properties', function(resp){
			return resp.data.data;
		});
	}
	
	self.saveConfiguration = function(serviceId,configuration){
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
				serviceId + '/' + domainId + '/execute', executionInput).then( function(resp){
			deferred.resolve(resp.data.value);
		});
		 return deferred.promise;
	}
})

