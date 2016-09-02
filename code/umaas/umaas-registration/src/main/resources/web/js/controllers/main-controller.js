angular.module('app')
.controller('MainCtrl', function($scope, umaas, $state){
	console.log("Successfully Loaded");
	$scope.domain = new umaas.Domain();
	console.log($scope.domain);
	umaas.domains.findAll({size:2}, function(error, domains){
		if(error){
			console.log("Error");
			console.log(error);
		}else{
			console.log("Success");
			console.log(domains);
		}
	})
	
	$scope.go = function(stateStr){
		$state.go(stateStr);
	}
})