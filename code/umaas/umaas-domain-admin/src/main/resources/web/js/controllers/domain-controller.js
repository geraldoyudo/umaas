angular.module('app')

.controller('DomainCtrl', function($scope, domain, loader, $http, fields){
	$scope.domain = domain;
	$scope.fields = fields;
	$scope.generateCode = function(){
	    $http.get('/umaas/core/domain/domainAdmin/generateCode', {
	        params: { domain: $scope.domain.id }
	    }).then(function(resp){
	          $scope.domain.code = resp.data.code;
	          alert('Domain code generated!!')
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
			alert('Changes saved successfully!!');
		})
	}
})