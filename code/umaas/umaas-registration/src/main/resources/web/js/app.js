angular.module('app', [])

.factory('umaas', function($window){
	return $window.umaas;
})
.controller('SampleController', function($scope, umaas){
	console.log("Successfully Loaded");
	$scope.domain = new umaas.Domain();
	console.log($scope.domain);
})
