angular.module('app')

.config(function($stateProvider, $urlRouterProvider,
		DomainConstants, domainFields) {
    
	$stateProvider
		.state('users', {
	        templateUrl: '/app/partials/users.html',
	        controller: 'EntityCtrl',
	        resolve: {
            	domain: function(loader){
            		return loader.loadDomain();
            	},
            	fields: function(loader){
            		return loader.loadFields();
            	},
            	entityType: function(){return DomainConstants.Entity.User}
            }
	      })
          .state('groups',{
              templateUrl: '/app/partials/groups.html',
              controller: 'EntityCtrl',
	  	      resolve: {
	              	domain: function(loader){
	              		return loader.loadDomain();
	              	},
	              	fields:function(){return {}},
	              	entityType: function(){return DomainConstants.Entity.Group}
	  	      }
          })
          .state('roles',{
            templateUrl: '/app/partials/roles.html',
	    	  controller: 'EntityCtrl',
	  	      resolve: {
	              	domain: function(loader){
	              		return loader.loadDomain();
	              	},
	              	fields:function(){return {}},
	              	entityType: function(){return DomainConstants.Entity.Role}
	  	      }
          })
          .state('customFields',{
            templateUrl: '/app/partials/custom-fields.html',
            controller: 'EntityCtrl',
	  	      resolve: {
	              	domain: function(loader){
	              		return loader.loadDomain();
	              	},
	              	fields:function(){return {}},
	              	entityType: function(){return DomainConstants.Entity.Field}
	  	      }
          
          })
          .state('properties',{
              templateUrl: '/app/partials/properties.html',
              controller: 'DomainCtrl',
	  	      resolve: {
	              	domain: function(loader){
	              		return loader.loadDomain();
	              	},
	              	fields:function(){return angular.copy(domainFields)}	  	      
	          }
          });
        
    // catch all route
    // send users to the form page 
    $urlRouterProvider.otherwise('/users');
})