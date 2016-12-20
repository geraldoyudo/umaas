var arrayInputCtrl = function($scope){
	var key = $scope.to.key || 'name';
	var optionKey = $scope.to.optionKey || key;
	$scope.searchText = '';
	$scope.searchObject = {};
	$scope.searchObject[optionKey] = $scope.searchText;
	$scope.transform = function($chip){
		if(key){
			return $chip[key];
		}else{
			return $chip
		}
	}
}

angular.module('app')
.run(function(formlyConfig, globalConfig){
    formlyConfig.setType({
        name: 'arrayInput',
        templateUrl: globalConfig.basePath + '/app/partials/templates/array-input.htm',
        controller: arrayInputCtrl
    });
  
	formlyConfig.setType({
	  name: 'file-input',
	  templateUrl: globalConfig.basePath + '/app/partials/templates/file-input.htm'
	});
    
})


.service('loader', function($q, globalConfig, $window){
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
					$window.location = "./login?error=403";
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
	this.setDomain = function(d){
		domain = d;
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
	var groups;
	this.loadGroups = function(){
		var deferred = $q.defer();
		if(groups){
			deferred.resolve(groups);
		}else{
			umaas.groups.find({size:1000}, function(error, g){
				if(error){
					console.log("Error");
					console.log(error);
					deferred.reject();
				}else{
					console.log("Success");
					groups = g.content;
					console.log(groups);
					deferred.resolve(groups);
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
})

.config(function($httpProvider){
	$httpProvider.interceptors.push(function($q, umaas) {
		var base = umaas.getBaseUrl();
		var auth, code;
		code = umaas.getAccessCode();
		auth = 'Basic ' + btoa( code.id + ":" +code.code);
	  return {
	   'request': function(config) {
	       console.log(config.url);
	      if( config.url.indexOf(base) !== -1){
	    	  console.log("Core request");
	    	  config.headers["Authorization"] = auth;    	 
	      }
	      return config;
	    }   
	  };
});
})