angular.module('app')

.service('stageManager', function($state, $rootScope){
	var self = this;
	self.stages = [];
	var highestIndex;
	
	var getNextStage = function(index, subIndex){
		subIndex = subIndex + 1;
		console.log(self.stages);
		for(var i =0; i< self.stages.length; ++i){
			if(self.stages[i].index === index && 
					self.stages[i].subIndex === subIndex){
				console.log("Stage");
				console.log(self.stages[i]);
				return self.stages[i];
			}
		}
		index = index +1;
		subIndex = -1;
		if(index > highestIndex){
			return;
		}
		return getNextStage(index,subIndex);
	}
	
	var getPreviousStage = function(index, subIndex){
		subIndex = subIndex -1;
		if(subIndex < 0){
			subIndex = 0;
			index = index -1;
			if( index < 0){
				return;
			}
		}
		for(var i =0; i< self.stages.length; ++i){
			if(self.stages[i].index === index && 
					self.stages[i].subIndex === subIndex){
				console.log("Stage");
				console.log(self.stages[i]);
				return self.stages[i];
			}
		}
		return getPreviousStage(index,subIndex);
	}
	
	var getHighestIndex = function(){
		var highest = 0;
		for(var i=0; i<self.stages.length; ++i){
			if(self.stages[i].index > highest){
				highest = self.stages[i].index;
			}
		}
		return highest;
	}
	var applyStage = function(stage, data){
		self.currentStage = stage;
		$state.go(stage.state);
		console.log("Applying stage");
		console.log(stage);
		console.log(data);
		$rootScope.$broadcast("Stage.Start", stage, data);
	}
	
	self.start = function(){
		console.log("Starting...");
		highestIndex = getHighestIndex();
		console.log("highest index");
		console.log(highestIndex);
		var stage = getNextStage(0,-1);
		console.log(stage)
		if(stage){
			applyStage(stage);
		}
	}
	
	self.next = function(data){
		if(!self.currentStage){
			return;
		}
		if(self.currentStage.listener){
			self.currentStage.listener(data, function(retVal){
				console.log("After listener");
				console.log(retVal);
				var stage = getNextStage(
						self.currentStage.index,
						self.currentStage.subIndex);
				applyStage(stage, retVal);
			});
		}	
	}
	
	self.previous = function(){
		if(!self.currentStage){
			return;
		}
		var stage = getPreviousStage(
				self.currentStage.index,
				self.currentStage.subIndex);
		applyStage(stage);
	}
	
	self.hasNext =function(){
		if(!self.currentStage){
			return false;
		}
		var stage = getNextStage(
				self.currentStage.index,
				self.currentStage.subIndex);
		if(stage) return true;
		else return false;
	}
	
	self.hasPrevious =function(){
		if(!self.currentStage){
			return false;
		}
		var stage = getPreviousStage(
				self.currentStage.index,
				self.currentStage.subIndex);
		if(stage) return true;
		else return false;
	}
	
});