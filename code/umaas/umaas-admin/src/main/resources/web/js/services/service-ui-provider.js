angular.module('app')

.provider('serviceUIProvider', function($q, umaas, $http, globalConfig){
	var domainId = umaas.getDomain().id;
	var self = this;
	var baseUrl = globalConfig.basePath;
	
	var checkNull = function(variable){
		if(!variable) throw "Null data present";
	}
	self.getServices = function(){
		return $http.get(baseUrl + '/serviceUI', function(resp){
			return resp.data.data;
		})
	}
	self.getDescription = function(serviceId){
		checkNull(serviceId);
		return $http.get(baseUrl + '/serviceUI/' + serviceId + '/description', function(resp){
			return resp.data.data;
		});
	}
	self.getTemplate = function(serviceId){
		checkNull(serviceId);
		return $http.get(baseUrl + '/serviceUI/' + serviceId + '/template', function(resp){
			return resp.data.data;
		});
	}
	self.isEnabled = function(serviceId){
		checkNull(serviceId);
		return $http.get(baseUrl + '/serviceUI/' + serviceId + '/enabled', function(resp){
			return resp.data.data;
		});
	}
})

