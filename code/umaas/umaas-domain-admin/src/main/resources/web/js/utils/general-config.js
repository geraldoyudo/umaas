angular.module('app')

.run(function(formlyConfig){
	formlyConfig.setType({
	  name: 'file-input',
	  templateUrl: '/app/partials/templates/file-input.htm'
	});
})


.service('loader', function($q, globalConfig){
	var domain;
	this.loadDomain = function(){
		var deferred = $q.defer();
		if(domain){
			deferred.resolve(domain);
		}else{
			umaas.setAccessCode(globalConfig.accessCode);
			umaas.domains.findByName(globalConfig.domainName, function(error, d){
				if(error){
					console.log("Error");
					console.log(error);
					deferred.reject();
				}else{
					console.log("Success");
					domain = d;
					console.log(domain);
					umaas.setDomain(domain);
					deferred.resolve(domain);
				}
			})
		}	
		return deferred.promise;
	}
	var fields;
	this.loadFields = function(){
		var deferred = $q.defer();
		if(fields){
			deferred.resolve(fields);
		}else{
			umaas.fields.find({size:1000}, function(error, f){
				if(error){
					console.log("Error");
					console.log(error);
					deferred.reject();
				}else{
					console.log("Success");
					fields = f.content;
					console.log(fields);
					deferred.resolve(fields);
				}
			})
		}	
		return deferred.promise;
	}
})

.constant("DomainConstants",{
    Entity:{
        User: "User",
        Domain: "AppDomain",
        Role: "Role",
        Field: "Field",
        Group: "Group",
        RoleMapping: "RoleMapping"
    }
});

