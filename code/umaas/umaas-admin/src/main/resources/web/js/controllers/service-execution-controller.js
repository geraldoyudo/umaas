angular.module('app')

.controller('ServiceExecutionCtrl', function($scope, 
		 customServiceAttendant, $state, globalConfig){
	$scope.state = $state.data;
	$scope.serviceUIs = Object.keys(globalConfig.serviceUINames);
	$scope.goTo = function(serviceId){
		$state.go("services." + serviceId);
	}
	$scope.getName = function(serviceId){
		return globalConfig.serviceUINames[serviceId];
	}
	$scope.execute = function(serviceId, method, input, variable){
		customServiceAttendant.execute(serviceId, method, input).then(function(value){
			console.log("Executed!!");
			if(variable){
				$scope[variable] = value;
			}
			alert("Completed!");
		});
		
	}
	
	
})