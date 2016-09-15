angular.module('app')

.config(function($stateProvider, $urlRouterProvider) {
    
	$stateProvider
	
		.state('users', {
	        templateUrl: '/app/partials/users.html'
	      })
          .state('groups',{
              templateUrl: '/app/partials/groups.html'
          })
          .state('roles',{
            templateUrl: '/app/partials/roles.html'
          })
          .state('customFields',{
            templateUrl: '/app/partials/custom-fields.html'
          
          })
          .state('properties',{
              templateUrl: '/app/partials/properties.html'
          })
           .state('roleMapping',{
              templateUrl: '/app/partials/role-mapping.html'
          });;
        
    // catch all route
    // send users to the form page 
    $urlRouterProvider.otherwise('/users');
})