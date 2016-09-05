angular.module('app')
.controller('RegistrationCtrl', function($scope,$rootScope, stageManager){
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
	$scope.display.title = stageManager.currentStage.title;
	$scope.display.description = stageManager.currentStage.description;
	$scope.display.hideNav = stageManager.currentStage.hideNav;


	$scope.next = function(){
		console.log("Next pressed");
		stageManager.next();
		refresh();		
	}
	$scope.previous = function(){
		console.log("Previous pressed");
		stageManager.previous();
		refresh();
	}
	$scope.$on('Stage.Changed', function(event, state){
		console.log("State Changed");
		$scope.display.title = state.title;
		$scope.display.description = state.description;
		$scope.display.hideNav = state.hideNav;
	})
})