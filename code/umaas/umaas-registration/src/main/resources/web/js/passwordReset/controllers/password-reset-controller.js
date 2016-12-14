angular.module("passwordReset")

.controller("PasswordResetCtrl", function($scope,
		umaas, $state, $http, globalConfig, domain){
	var vm = $scope;
	var tokenId;
	vm.data = {};
	vm.data.emailSent = false;
	var savedEmail;
	var savedUser;
	vm.loginPage = domain.properties.loginPage;
	
	var code = globalConfig.accessCode;
	var url =  umaas.getBaseUrl() + "/endpoint/com.gerald.umaas.domain.services.extensions.RegistrationService/" + 
	domain.id + "/properties";
	console.log(url);
	var auth = 'Basic ' + btoa( code.id + ":" +code.code);
	$http.get(url , {
	    headers: {'Authorization': auth}
	}).then(function(resp){
		vm.domainProperties = resp.data;
		console.log($scope.domainProperties);
	});
	
	vm.checkEmail = function(email){
		umaas.appUsers.findByEmail(email, function(error, user){
			if(error){
				console.log(error);
				alert("Invalid Email!");
			}else{
				savedUser = user;
				$http.post(globalConfig.basePath + '/app/verify/request', 
						{
							"name": "password-reset",
							"value": email,
							"properties": {
								"domain": globalConfig.domainName,
								"tokenValues":{
									"userId": user.id
								}
							}
						}).then(function(resp){
					var data = resp.data;
					console.log(data);
					tokenId = data.id;
					vm.data.emailSent = true;
					savedEmail = email;
					alert("Phone Reset Email Sent");
				});
			}
			
		});
	}
	
	 vm.resend = function(){
		 console.log("Executing Resend");
		$http.post(globalConfig.basePath + '/app/verify/resend', {
			"name": "password-reset",
			"value": savedEmail,
			"properties": {
				"domain": globalConfig.domainName,
				"tokenId": tokenId,
				"tokenValues":{
					"userId": savedUser.id
				}
			}
		}).then(function(resp){
			var data = resp.data;
			console.log(data);
			tokenId = data.id;
			alert("Password Reset code resent!!")
		});
	 }
	 
	 vm.verify = function(code){
		 console.log("Executing verify");
		 $http.post(globalConfig.basePath + '/app/verify/process', {
				"name": "password-reset",
				"value": savedEmail,
				"properties": {
					"code": code,
					"tokenId": tokenId
				}
			}).then(function(resp){
			    var data = resp.data;
			 	console.log(data);
			 	if(!data.verified){
			 		alert("Invalid reset code or your request has expired. Please make another request");
			 	}else{
			 		var userId = data.values.userId;
			 		$state.go("passwordReset.changePassword", {userId: userId});
			 
			 	}
				
			});
	 }
	 
	 
});