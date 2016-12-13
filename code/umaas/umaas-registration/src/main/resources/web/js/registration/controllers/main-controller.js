angular.module('app')
.controller('MainCtrl', function($scope, $state){
	console.log("Successfully Loaded");
	$scope.go = function(stateStr){
		$state.go(stateStr);
	}
})