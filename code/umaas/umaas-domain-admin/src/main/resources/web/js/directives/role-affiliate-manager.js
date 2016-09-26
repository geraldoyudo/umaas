angular.module('app')

.directive("roleAffiliateManager",function(){
    return {
        restrict: "AE",
        templateUrl: "/app/partials/templates/role-affiliate-manager.htm",
        scope:{
            role: "@role"
        },
        controller: function($scope, $q, umaas){
        	console.log($scope.role);
        	$scope.role = JSON.parse($scope.role);
        	$scope.directions = ['in', 'out'];
        	$scope.affiliates = ['USER', 'GROUP'];
        	$scope.options = [5,10,20,50,100];
        	$scope.directions.forEach( function(direction){
        		(function (direction) {
        			$scope[direction] = {};
            		$scope.affiliates.forEach(function(type){
            			(function (direction, type) {
            				$scope[direction][type] = {};
                			$scope[direction][type].paginationData = {size: 10,page:1};
                			$scope[direction][type].entities ={
                					content: []
                			}
            				$scope[direction][type].onPaginate = function(page, limit){
                				console.log("on paginiate ", page, limit);
                                $scope[direction][type].paginationData.page = page;
                                $scope[direction][type].paginationData.size = limit;
                                $scope.load(direction, type);
                			}
            				}(direction, type));
            			
            		})
        			}(direction));
        		
        	})
        	$scope.load = function(direction, type){
    			var deferred = $q.defer();
    			$scope[direction][type].loadingPromise= deferred.promise;
    			if(direction == 'out'){
    				var entityFn;
    				if(type == 'GROUP'){
    					entityFn = umaas.groups;
    				}else{
    					entityFn = umaas.appUsers;
    				}
    				entityFn.find({
	        			size: $scope[direction][type].paginationData.size,
	        			page: $scope[direction][type].paginationData.page -1
	        		},function(error, entities){
	            		console.log("loaded for " + direction + " and " +  type);
	            		$scope.$apply(function(){
	            			$scope[direction][type].entities = entities;
	            			if(!entities){
	            				$scope[direction][type].entities = new umaas.LazyList({_embedded:{roleMappings:[]},page:{size:$scope[direction][type].paginationData.size,totalElements:0,totalPages:1,number:0}});
	            			}
	            		})
	        			
	        			console.log($scope[direction][type].entities);
	        			deferred.resolve();
	        		});
    				return;
    			}
    			$scope.role.getWithCriteria({
        			size: $scope[direction][type].paginationData.size,
        			page: $scope[direction][type].paginationData.page -1
        		}, type, (direction === 'in') , function(error, entities){
            		console.log("loaded for " + direction + " and " +  type);
            		$scope.$apply(function(){
            			$scope[direction][type].entities = entities;
            			if(!entities){
            				$scope[direction][type].entities = new umaas.LazyList({_embedded:{roleMappings:[]},page:{size:$scope[direction][type].paginationData.size,totalElements:0,totalPages:1,number:0}});
            			}
            		})
        			
        			console.log($scope[direction][type].entities);
        			deferred.resolve();
        		})	
        		
        		
        	}            
        	$scope.remove = function(key, type){
        		$scope.role.removeKey(key, function(error){
        			if(error){alert('Error!'); return}
        			alert('Removed successfully.');
        			$scope.$apply(function(){
        				$scope.load('in', type);
        				$scope.load('out', type);
        			});
        			
        		});
        	}
        	$scope.add = function(key, type){
        		$scope.role.addKey(key, function(error){
        			if(error){alert('Error!'); return}
        			alert('Added successfully.');
        			$scope.$apply(function(){
        				$scope.load('in', type);
        				$scope.load('out', type);
        			})
        			
        		}, type);
        	}
        	
        	umaas.roles.findById($scope.role.id, function(error, role){
        		if(error){
        			throw error;
        		}
        		$scope.role = role;
        		$scope.load('in', 'USER');
        		$scope.load('out', 'USER');
        		$scope.load('in', 'GROUP');
        		$scope.load('out', 'GROUP');
        	});
        	
        	$scope.getContent = function(direction, type){
        		console.log(direction);
        		console.log(type);
        		return $scope[direction][type].entities.content;
        	}
        	$scope.containsRole = function (name, entity){
        		if(entity.roles){
            		return (entity.roles.indexOf(name) !== -1);
        		}else{
        			return false;
        		}
        	}
        	console.log($scope);
        }
    };
});