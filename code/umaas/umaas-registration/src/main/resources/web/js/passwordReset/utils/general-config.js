angular.module('passwordReset')

.service('loader', function($q, globalConfig){
	this.loadDomain = function(){
		var deferred = $q.defer();
		umaas.setAccessCode(globalConfig.accessCode);
		umaas.domains.findByName(globalConfig.domainName, function(error, domain){
			if(error){
				console.log("Error");
				console.log(error);
				deferred.reject();
			}else{
				console.log("Success");
				console.log(domain);
				umaas.setDomain(domain);
				deferred.resolve(domain);
			}
		})
		return deferred.promise;
	}
})


