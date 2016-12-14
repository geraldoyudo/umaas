angular.module('passwordReset')

.service('loader', function($q, globalConfig, umaas, $http){
	this.loadDomain = function(){
		var deferred = $q.defer();
		umaas.setAccessCode(globalConfig.accessCode);
		umaas.domains.findByName(globalConfig.domainName, function(error, domain){
			if(error){
				console.log("Error");
				console.log(error);
				deferred.reject();
			}else{
				var code = globalConfig.accessCode;
				var url =  umaas.getBaseUrl() + "/endpoint/com.gerald.umaas.domain.services.extensions.RegistrationService/" + 
				domain.id + "/properties";
				console.log(url);
				var auth = 'Basic ' + btoa( code.id + ":" +code.code);
				$http.get(url , {
				    headers: {'Authorization': auth}
				}).then(function(resp){
					domain.domainProperties = resp.data;
					console.log(domain.domainProperties);
					console.log("Success");
					console.log(domain);
					umaas.setDomain(domain);
					deferred.resolve(domain);
				});
			}
		})
		return deferred.promise;
	}
})


