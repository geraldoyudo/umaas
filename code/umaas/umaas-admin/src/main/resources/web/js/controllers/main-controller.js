angular.module('app')
.controller('MainCtrl', function($scope, $state, loader){
	console.log("Successfully Loaded");
	$state.go('users');
	loader.loadDomain().then(function(domain){
		$scope.domain = domain;
	});
})

           
.controller("layoutCtrl", function($mdSidenav, $scope, $state){
   
    $scope.toggleSidenav =  function(menuId) {
        $mdSidenav(menuId).toggle();
      };
      
    $scope.menu = [{icon:"person", title:"Users"},
    {icon:"group", title:"Groups"}, 
    {icon:"work", title:"Roles"},
     {icon:"view list", title:"Custom Fields"},
     {icon:"settings", title:"Setting"}, 
     {icon:"settings", title:"Configuration"},
     {icon:"settings", title:"Services"}];
    var routes=["users", "groups","roles", "customFields", "properties", "configuration", "services"];
    $scope.loadContent = function(index){
         $state.go(routes[index]);
     };
  });
