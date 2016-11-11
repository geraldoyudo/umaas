angular.module('app')

.controller('ServiceExecutionCtrl', function($scope, 
		 customServiceAttendant, domain){
	
	$scope.execute = function(serviceId, method, input){
		return customServiceAttendant.execute(serviceId, method, input);
	}
	
	
})