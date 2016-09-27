angular.module('app')

.run(function(stageManager, umaas, $http){
	var auth, code;
	code = umaas.getAccessCode();
	auth = 'Basic ' + btoa( code.id + ":" +code.code);
	var handleDetailsCapture = function(user, callback){
		console.log("Details capture");
		console.log(user);
		return callback(user);
	}
	var handleVerification = function(user, callback){
		var files = user.files;
		console.log("Details Verified");
		console.log(user);
		var domain = umaas.getDomain();
		if(domain.properties.emailAsUsername){
			user.username = user.email;
		}
	    
		user.insert(function(error,updatedUser){
			if(!error){ 
				user = updatedUser;
				console.log(user);
				if(!files)
					return callback(user);
				var fieldIds = Object.keys(files);
				var formData = new FormData();
				for(var i=0; i<fieldIds.length; ++i){
					console.log(files[fieldIds[i]]);
					var f = files[fieldIds[i]][0].lfFile;
					console.log(f);
					formData.append(fieldIds[i], f);
				}
				console.log(formData);
				var url = umaas.getBaseUrl() + '/files/user/upload/' + user.id;
				
				 $http.post(url, formData, {
		                headers: {'Content-Type': undefined, 'Authorization': auth}
		            }).then(function(result){
		                callback(user);               
		            },function(err){
		                throw err;
		            });
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