angular.module("passwordReset")

.controller("PasswordChangeCtrl", function($scope,
		umaas, $state, $stateParams, $http, globalConfig){
	var vm = $scope;
	
	if($stateParams.userId){
		umaas.appUsers.findById($stateParams.userId, function(error, appUser){
			if(error){
				alert("Invalid User ID");
			}else{
				vm.user = appUser;
			}
		});
	}else{
		console.log("Validating the tokens");
		console.log($stateParams);
		 $http.post(globalConfig.basePath + '/app/verify/process', {
				"name": "password-reset",
				"properties": {
					"code": $stateParams.code,
					"tokenId": $stateParams.tokenId
				}
			}).then(function(resp){
			    var data = resp.data;
			 	console.log(data);
			 	if(!data.verified){
			 		alert("Invalid token code or your request has expired. Please make another request");
			 		$state.go("passwordReset.enterEmail");
			 	}else{
			 		var userId = data.values.userId;
			 		umaas.appUsers.findById(userId, function(error, appUser){
						if(error){
							alert("Invalid User ID");
						}else{
							vm.user = appUser;
						}
					});
			 	}
				
			});
	}
	
	
	vm.changePassword = function(newPassword){
		if(!newPassword) throw "Password is undefined!!";	
		if(vm.user){
			vm.user.update({password: newPassword}, function(error, appUser){
				 if(error){
					 alert("Password change failed");
					 $state.go("passwordReset.enterEmail");
				 }else{
					 $state.go("passwordReset.success");
				 }
			})
		}
	}
	 
});