angular.module('app')
.controller('EntityCtrl', function($scope,
		domain, fields, entityType, entityAttendant, 
		$q, $mdDialog, entityListener, globalConfig){
	
	 var displayForm = function($event, entity, newEntity){
		 	console.log("Displaying form");
	        var parentEl = angular.element(document.body);
	        console.log(parentEl);
	        $scope.newEntity = newEntity;
	        console.log("About to show");
	        $mdDialog.show({
	            parent: parentEl,
	            targetEvent: $event,
	            templateUrl: globalConfig.basePath + '/app/partials/templates/EntityEditForm.htm',
	            controller: 'EntityEditFormCtrl',
	            locals: { 
	                isNew: newEntity,
	                entity: entity,
	                entityType: entityType
	            }
	           
	        }).then(function (ret) {
	           var mode = ret.mode;
	           var entity = ret.entity;
	          
	            if(mode === "delete"){
	                $scope.delete(entity);
	            }else if(mode === "update"){
	               $scope.update(entity);
	            }else if(mode === "add"){
	               $scope.add(entity);
	            }
	           
	          }, function () {
	            console.log('Modal dismissed at: ' + new Date());
	          });
	        
	      
	    };
	    
	console.log("Successfully Loaded entity controller for " + entityType);
	$scope.paginationData = {size: 10,page:1};
	$scope.fields = fields;
	$scope.createNewEntity = function(){
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
	$scope.add = function(entity){
		entity.insert(function(e, insertedEntity){
			if(e){
				console.log(e);
				alert("Error!");
				return;
			}
			if(entityListener[entityType] && entityListener[entityType].onInsert){
				insertedEntity.files = entity.files;
				entityListener[entityType].onInsert(insertedEntity).then(function(e){
					alert("Inserted !");
					entity = insertedEntity;
					$scope.load();
				})
			}else{
				alert("Inserted !");
				$scope.load();
			}
		});
	};
	
	$scope.delete = function(entity){
		entity.delete(function(e){
			if(e){
				alert("Error!");
				return
			}
			if(entityListener[entityType] && entityListener[entityType].onDelete){
				entityListener[entityType].onDelete(entity).then(function(){
					alert("Deleted !");
					$scope.load();
				})
			}else{
				alert("Deleted !");
				$scope.load();
			}
			
		});
	};
	
	$scope.update = function(entity){
		entity.sync(function(e, updatedEntity){
			if(e){
				alert("Error!");
				console.log(e);
				return;
			}
			if(entityListener[entityType] && entityListener[entityType].onUpdate){
				updatedEntity.files = entity.files;
				entityListener[entityType].onUpdate(updatedEntity).then(function(){
					alert("Updated !");
					entity = updatedEntity
					$scope.load();
				})
			}else{
				alert("Updated !");
				$scope.load();
			}
		});
	};
	 $scope.cancel = function(){
	      $mdDialog.cancel();
    };
    $scope.showEntityForm = function($event, entity){
        var newE = false;
        if(!entity){
            entity = $scope.createNewEntity();
            newE = true;
        }
        displayForm($event, entity, newE);
    };
})


.controller('EntityEditFormCtrl', function ($scope, $mdDialog,
		entity,isNew, entityType, EntityFormFieldsFactory) {
  console.log("Entity Edit form controller");
  $scope.entity = angular.copy(entity);
  EntityFormFieldsFactory.create(entityType,isNew).then(function(fields){
	  $scope.fields = fields;
  })
  $scope.newEntity = isNew;
  $scope.data = {};
  $scope.data.isNew = isNew;
  console.log("New")
  console.log($scope.data.isNew);
  console.log("Entity fields");
  console.log($scope.fields);
  console.log("Entity object");
  console.log($scope.entity);
  $scope.delete= function (entity) {
      console.log("Sending delete message");
      var ret = {
          entity: entity,
          mode: 'delete'
      };
       $mdDialog.hide(ret);
  };
  $scope.update = function(entity){
      console.log("Sending update message");
      var ret = {
          entity: entity,
          mode: 'update'
      };
       $mdDialog.hide(ret);
  };
  $scope.add = function(entity){
      console.log("Sending add message");
      var ret = {
          entity: entity,
          mode: 'add'
      };
       $mdDialog.hide(ret);
  };
  $scope.cancel = function () {
    $mdDialog.cancel();
  };
})

.factory('entityListener', function(DomainConstants, $q, $http){
	var auth, code;
	code = umaas.getAccessCode();
	auth = 'Basic ' + btoa( code.id + ":" +code.code);
	var e = {};
	var uploadFiles = function($q, user){
		console.log(user);
		var deferred = $q.defer();
		var files = user.files;
		if(!files){ 
			deferred.resolve(user);
			return deferred.promise;
		}
		var fieldIds = Object.keys(files);
		if(fieldIds.length === 0){
			deferred.resolve(user);
			return;
		}
		var formData = new FormData();
		for(var i=0; i<fieldIds.length; ++i){
			console.log(files[fieldIds[i]]);
			if(files[fieldIds[i]].length ===0 || ! files[fieldIds[i]][0].lfFile)
				continue;
			var f = files[fieldIds[i]][0].lfFile;
			console.log(f);
			formData.append(fieldIds[i], f);
		}
		console.log(formData);
		var url = umaas.getBaseUrl() + '/files/user/upload/' + user.id;
		
		 $http.post(url, formData, {
                headers: {'Content-Type': undefined, 'Authorization': auth}
            }).then(function(result){
                deferred.resolve(user);    
                return;
            },function(err){
                deferred.reject( err);
                return;
            });
		 return deferred.promise;
	}
	e[DomainConstants.Entity.User] = {
			onUpdate: function(user){
				console.log("On Update");
				console.log(user);
				return uploadFiles($q, user);
			},
			onInsert: function(user){
				console.log("On Insert");
				console.log(user);
				return uploadFiles($q, user);
			}
	}
	return e;
})