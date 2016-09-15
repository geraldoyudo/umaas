angular.module('app')
.controller('EntityCtrl', function($scope,
		domain, fields, entityType, entityAttendant, $q){
	console.log("Successfully Loaded entity controller for " + entityType);
	$scope.paginationData = {size: 10,page:1};
	$scope.fields = fields;
	$scope.newEntity = function(){
		return entityAttendant.newEntity(entityType);
	}
	$scope.entities={
			content: []
	}
	$scope.options = [5,10,20,50,100];
	$scope.load = function(){
		var deferred = $q.defer();
		$scope.loadingPromise = deferred.promise;
		entityAttendant.getEntities(entityType, {
			size: $scope.paginationData.size,
			page: $scope.paginationData.page -1
		}).then(function(entities){
			$scope.entities = entities;
			console.log($scope.entities);
			deferred.resolve();
		})
	}
	$scope.selectedEntities =[];
    $scope.onSelect = function(entity){
        console.log("on select",entity)
        $scope.selectedEntities =[entity];
    };
    $scope.onPaginate= function(page, limit){
        console.log("on paginiate ", page, limit);
        $scope.paginationData.page = page;
        $scope.paginationData.size = limit;
        $scope.load();
    };
	$scope.load();
	$scope.addEntity = function(entity){
		entity.insert(function(error, insertedEntity){
			if(error){
				alert("Error!");
			}
			entity = insertedEntity;
			alert("Inserted !");
			$scope.load();
		});
	};
	
	$scope.deleteEntity = function(entity){
		entity.delete(function(error){
			if(error){
				alert("Error!");
			}
			alert("Deleted !");
			$scope.load();
		});
	};
	
	$scope.updateEntity = function(entity){
		entity.update(function(error, updatedEntity){
			if(error){
				alert("Error!");
			}
			entity = updatedEntity
			alert("Updated !");
			$scope.load();
		});
	};
	
	
	
	
})