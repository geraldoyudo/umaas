angular.module('app')

.config(function($stateProvider, $urlRouterProvider,
		DomainConstants, domainFields, globalConfig) {
   
	$stateProvider
		.state('users', {
	        templateUrl: globalConfig.basePath + '/app/partials/users.html',
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
              templateUrl: globalConfig.basePath + '/app/partials/groups.html',
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
            templateUrl: globalConfig.basePath + '/app/partials/roles.html',
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
            templateUrl: globalConfig.basePath + '/app/partials/custom-fields.html',
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
              templateUrl: globalConfig.basePath + '/app/partials/properties.html',
              controller: 'DomainCtrl',
	  	      resolve: {
	              	domain: function(loader){
	              		return loader.loadDomain();
	              	},
	              	fields:function(){return angular.copy(domainFields)}	  	      
	          }
          })
          .state('configuration',{
              templateUrl: globalConfig.basePath + '/app/partials/configuration.html',
              controller: 'ServiceConfigCtrl',
	  	      resolve: {
	              	domain: function(loader){
	              		return loader.loadDomain();
	              	}	  	      
	          }
          })
          .state('services',{
              templateUrl: globalConfig.basePath + '/app/partials/services.html',
              controller: 'ServiceExecutionCtrl'
          });   
          
		var serviceIds = Object.keys(globalConfig.serviceUINames);
		console.log(serviceIds);
		serviceIds.forEach(function(serviceId){
			$stateProvider.state('services.' + serviceId, {
				templateUrl: globalConfig.basePath + '/app/serviceUI/' + serviceId + '/template',
				data: {
					name: globalConfig.serviceUINames[serviceId],
					description: globalConfig.serviceUIDescriptions[serviceId]
				}
			})
		})
        
    // catch all route
    // send users to the form page 
    $urlRouterProvider.otherwise('/properties');
})