angular.module('app')
.controller('MainCtrl', function($scope, $state, loader){
	console.log("Successfully Loaded");
	loader.loadDomain().then(function(domain){
		$scope.domain = domain;
	});
})

           
.controller("layoutCtrl", function($mdSidenav, $scope, 
		$state){
   
    $scope.toggleSidenav =  function(menuId) {
        $mdSidenav(menuId).toggle();
      };
      
    $scope.menu = [{icon:"person", title:"Users"},
    {icon:"group", title:"Groups"}, 
    {icon:"work", title:"Roles"},
     {icon:"text_fields", title:"Custom Fields"},
     {icon:"settings", title:"Setting"}, 
     {icon:"settings_applications", title:"Configuration"},
     {icon:"developer_mode", title:"Services"}];
    var routes=["users", "groups","roles", "customFields", "properties", "configuration", "services"];
    $scope.loadContent = function(index){
    	console.log(routes[index]);
         $state.go(routes[index]);
     };
     
     $state.go("properties");
     $scope.$on('$stateChangeStart', function(e, toState, toParams, fromState, fromParams) {
    	   if($mdSidenav("left").isOpen()){
    		   $mdSidenav("left").close();
    	   }
    	});
  });
