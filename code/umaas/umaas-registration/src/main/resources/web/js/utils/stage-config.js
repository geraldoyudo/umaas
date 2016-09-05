angular.module('app')

.run(function(stageManager){
	var stages = [
	              {
	            	  index: 0,
	            	  description: "Enter Details",
	            	  title: "Details Form",
	            	  subIndex: 0,
	            	  state: "form.details"
	              },
	              {
	            	  index: 1,
	            	  description: "Click 'verify' to verify details one at a time",
	            	  title: "Details Verification",
	            	  subIndex: 0,
	            	  state: "form.verification"  
	              },
	              {
	            	  index: 2,
	            	  description: "You have successfully registered.",
	            	  title: "Registration Success!!",
	            	  subIndex: 0,
	            	  state: "form.success",
	            	  hideNav: true
	              }
	             ];
	
	stageManager.stages = stages;
})