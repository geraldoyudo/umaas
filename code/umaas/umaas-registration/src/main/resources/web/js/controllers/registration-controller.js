angular.module('app')
.controller('RegistrationCtrl', function($scope,$rootScope, 
		stageManager,umaas, fieldManager){
	console.log("Registration controller");
	var refresh = function(){
		$scope.hasPrevious = stageManager.hasPrevious();
		$scope.hasNext = stageManager.hasNext();
	}
	$scope.display = {};
	var start = function(){
		stageManager.start();
		refresh();
	};
	start();
	$scope.user = new umaas.AppUser();
	$scope.display.title = stageManager.currentStage.title;
	$scope.display.description = stageManager.currentStage.description;
	$scope.display.hideNav = stageManager.currentStage.hideNav;


	$scope.next = function(data){
		console.log("Next pressed");
		stageManager.next(data);
		refresh();		
	}
	$scope.previous = function(){
		console.log("Previous pressed");
		stageManager.previous();
		refresh();
	}
	$scope.$on('Stage.Start', function(event, stage, data){
		console.log("State Changed");
		$scope.display.title = stage.title;
		$scope.display.description = stage.description;
		$scope.display.hideNav = stage.hideNav;
		if(data){
			$scope.user = data;
		}
	})
	fieldManager.getFields().then(function(fields){
		console.log("Fields loaded");
		console.log(fields);
		$scope.fields = fields;
	})
	
})