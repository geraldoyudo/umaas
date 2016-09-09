angular.module('app')

.run(function(formlyConfig){
	formlyConfig.setType({
	  name: 'file-input',
	  templateUrl: '/app/partials/templates/file-input.htm'
	});
})


.run(function(formlyConfig){
	formlyConfig.setType({
	  name: 'verify-item',
	  templateUrl: '/app/partials/templates/verify-item.htm',
	  controller: function($scope, $parse, $http){
		  var key = $scope.options.key;
		  if(key){
			  $scope.modelValue = $scope.model[key];
		  }else{
			  var expression = $scope.options.ngModelELAttrs["ng-model"];
			  if(!expression) throw new Error("No expression to resolve");
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
			$http.post('/app/verify/request', request).then(function(resp){
				var data = resp.data;
				console.log(data);
				request.properties.tokenId = data.id;
				$scope.verifyRequested = true;
			});
		 }
		 $scope.resend = function(){
			 console.log("Executing Resend");
			 $scope.verificationFailed = false;
			$http.post('/app/verify/resend', request).then(function(resp){
				var data = resp.data;
				console.log(data);
				request.properties.tokenId = data.id;
				$scope.verifyRequested = true;
			});
		 }
		 $scope.verify = function(){
			 console.log("Executing verify");
			 request.properties.code = $scope.data.code;
			 $http.post('/app/verify/process', request).then(function(resp){
				    var data = resp.data;
				 	console.log(data);
					$scope.verified = data.verified;
					$scope.verificationFailed = !data.verified;
					if($scope.verified){
						alert("Verification success");
					}else{
						alert("Verification failed");
					}
				});
		 }
	  }
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


