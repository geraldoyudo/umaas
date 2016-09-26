angular.module('app')

.run(function(formlyConfig, globalConfig){
	formlyConfig.setType({
		  name: 'file-input',
		  templateUrl: globalConfig.basePath + '/app/partials/templates/file-input.htm'
		});
	formlyConfig.setType({
	  name: 'verify-item',
	  templateUrl: globalConfig.basePath + '/app/partials/templates/verify-item.htm',
	  controller: function($scope, $parse, $http){
		  var key = $scope.options.key;
		  console.log($scope);
		  $scope.form.$setValidity($scope.options.name, false);
		  if((typeof key) === 'string'){
			  $scope.modelValue = $scope.model[key];
		  }else{
			  var expression = $scope.options.templateOptions.key;
			  if(!expression) throw new Error("No expression to resolve");
			  console.log("Expression " + expression);
			  var getter = $parse(expression);
			  $scope.modelValue = getter($scope);
		  }
		
		 var name = $scope.options.templateOptions.verifier;
		 if(!name) throw new Error("No verifier set");
		 $scope.data ={};
		 $scope.data.code = "0000";
		 var request = {};
		 request.name = name;
		 request.value = $scope.modelValue;
		 request.properties = {};
		 $scope.verified = false;
		 $scope.verifyRequested = false;
		 $scope.requestVerify = function(){
			 console.log("Executing Request verify");
			 $scope.verificationFailed = false;
			$http.post(globalConfig.basePath + '/app/verify/request', request).then(function(resp){
				var data = resp.data;
				console.log(data);
				request.properties.tokenId = data.id;
				$scope.verifyRequested = true;
			});
		 }
		 $scope.resend = function(){
			 console.log("Executing Resend");
			 $scope.verificationFailed = false;
			$http.post(globalConfig.basePath + '/app/verify/resend', request).then(function(resp){
				var data = resp.data;
				console.log(data);
				request.properties.tokenId = data.id;
				$scope.verifyRequested = true;
				alert("Verification code resent!!")
			});
		 }
		 $scope.verify = function(){
			 console.log("Executing verify");
			 request.properties.code = $scope.data.code;
			 $http.post(globalConfig.basePath + '/app/verify/process', request).then(function(resp){
				    var data = resp.data;
				 	console.log(data);
					$scope.verified = data.verified;
					$scope.verificationFailed = !data.verified;
					if(!$scope.verified){
						alert("Verification failed");
					}
					$scope.form.$setValidity($scope.options.name, true);
				});
		 }
	  }
	});
})

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


