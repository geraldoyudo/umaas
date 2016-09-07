angular.module('app')

.run(function(stageManager, umaas){
	var handleDetailsCapture = function(user, callback){
		console.log("Details capture");
		console.log(user);
		return callback(user);
	}
	var handleVerification = function(user, callback){
		console.log("Details Verified");
		console.log(user);
		user.insert(function(error,updatedUser){
			if(!error){ 
				user = updatedUser;
				console.log(user);
				return callback(user);
			}else{
				throw error;
			}
		})	
	}
	var stages = [
	              {
	            	  index: 0,
	            	  description: "Enter Details",
	            	  title: "Details Form",
	            	  subIndex: 0,
	            	  state: "form.details",
	            	  listener: handleDetailsCapture
	              },
	              {
	            	  index: 1,
	            	  description: "Click 'verify' to verify details one at a time",
	            	  title: "Details Verification",
	            	  subIndex: 0,
	            	  state: "form.verification",
	            	  listener: handleVerification
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