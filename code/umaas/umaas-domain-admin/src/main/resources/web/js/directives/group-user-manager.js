angular.module('app')

.directive("groupUserManager",function(){
    return {
        restrict: "AE",
        templateUrl: globalConfig.basePath + "/app/partials/templates/group-user-manager.htm",
        scope:{
            group: "@group"
        },
        controller: function($scope, $q, umaas){
        	console.log($scope.group);
        	$scope.group = JSON.parse($scope.group);
        	$scope.in = {};
        	$scope.out = {};
        	$scope.in.paginationData = {size: 10,page:1};
        	$scope.in.users ={
        			content: []
        	}
        	$scope.out.paginationData = {size: 10,page:1};
        	$scope.out.users ={
        			content: []
        	}
        	$scope.options = [5,10,20,50,100];
        	$scope.load = function(isIn){
        		var deferred = $q.defer();
        		if(isIn){
        			$scope.in.loadingPromise = deferred.promise;
        			$scope.group.getUsers({
            			size: $scope.in.paginationData.size,
            			page: $scope.in.paginationData.page -1
            		}, function(error, users){
            			$scope.in.users = users;
            			console.log($scope.in.users);
            			deferred.resolve();
            		})
        		}else{
        			$scope.out.loadingPromise = deferred.promise;
        			umaas.appUsers.find({
            			size: $scope.out.paginationData.size,
            			page: $scope.out.paginationData.page -1
            		}, function(error, users){
            			$scope.out.users = users;
            			console.log($scope.out.users);
            			deferred.resolve();
            		})
        		}
        		
        	}
            $scope.in.onPaginate= function(page, limit){
                console.log("on paginiate ", page, limit);
                $scope.in.paginationData.page = page;
                $scope.in.paginationData.size = limit;
                $scope.load(true);
            };
            $scope.out.onPaginate= function(page, limit){
                console.log("on paginiate ", page, limit);
                $scope.out.paginationData.page = page;
                $scope.out.paginationData.size = limit;
                $scope.load(false);
            };
            
        	$scope.remove = function(userId){
        		$scope.group.removeUser(userId, function(error){
        			if(error){alert('Error!'); return}
        			alert('User removed successfully.');
        			$scope.$apply(function(){
        				$scope.load(true);
        				$scope.load(false);
        			})
        			
        		})
        	}
        	$scope.add = function(userId){
        		$scope.group.addUser(userId, function(error){
        			if(error){alert('Error!'); return}
        			alert('User added successfully.');
        			$scope.$apply(function(){
        				$scope.load(true);
        				$scope.load(false);
        			})
        			
        		})
        	}
        	
        	$scope.containsGroup = function (name, entity){
        		if(entity.groups){
            		return (entity.groups.indexOf(name) !== -1);
        		}else{
        			return false;
        		}
        	}
        	umaas.groups.findById($scope.group.id, function(error, group){
        		$scope.group = group;
        		$scope.load(true);
        		$scope.load(false);
        	})
        	
        }
    };
});