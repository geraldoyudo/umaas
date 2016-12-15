angular.module('app')

.controller('ServiceConfigCtrl', function($scope, 
		serviceConfigFieldFactory, customServiceAttendant){
	$scope.services = []
	customServiceAttendant.getServices().then(function(services){
		$scope.services = services.data;
		console.log($scope.services);
		var configurationSpecsMap = {};
		$scope.serviceFieldMap = {};
		$scope.data = {};
		$scope.data.configMap = {};
		$scope.data.selectedService = "com.gerald.umaas.domain.services.extensions.RegistrationService";
		$scope.services.forEach(function(service){
			customServiceAttendant.getConfigurationSpecs(service.id).then(function(specs){
				configurationSpecsMap[service.id] = specs.data;
				console.log(configurationSpecsMap[service.id]);
				var field = serviceConfigFieldFactory.create(specs.data);
				$scope.serviceFieldMap[service.id] = field;
				console.log($scope.serviceFieldMap);
				
			});
			customServiceAttendant.getConfiguration(service.id).then(function(configuration){
				$scope.data.configMap[service.id] = configuration.data;
				console.log($scope.data.configMap);
			});
		});
		
		$scope.saveConfiguration = function(serviceId, configuration){
			customServiceAttendant.saveConfiguration(serviceId, configuration).then(function(){
				alert("Configuration saved");
			}, function(){
				alert("Error occured");
			})
		}
		
	})
	
	
})