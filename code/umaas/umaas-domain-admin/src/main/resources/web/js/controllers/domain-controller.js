angular.module('app')

.controller('DomainCtrl', function($scope, domain, loader, $http, fields){
	$scope.domain = domain;
	$scope.fields = fields;
	$scope.generateCode = function(){
	    $http.get('/umaas/core/domains/domain/generateCode').then(function(resp){
	          $scope.domain.code = resp.data.code;
	      });
	}
	
	$scope.saveDomain = function(){
		domain.sync(function(error, domain){
			if(error){
				alert('Error');
				console.log(error);
				return;
			}
			$scope.domain = domain;
			loader.setDomain(domain);
		})
	}
})