angular.module('app')

.run(function(formlyConfig){
	formlyConfig.setType({
	  name: 'file-input',
	  templateUrl: '/app/partials/templates/file-input.htm'
	});
})

.service('loader', function($q){
	this.loadDomain = function(){
		var deferred = $q.defer();
		umaas.domains.findAll({size:2}, function(error, domains){
			if(error){
				console.log("Error");
				console.log(error);
				deferred.reject();
			}else{
				console.log("Success");
				console.log(domains);
				umaas.setDomain(domains.content[0]);
				deferred.resolve(domains.content[0]);
			}
		})
		return deferred.promise;
	}
})


