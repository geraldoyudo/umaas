angular.module('app')

.config(function($stateProvider, $urlRouterProvider) {
    
    $stateProvider
    
        // route to show our basic form (/form)
        .state('form', {
            url: '/form',
            templateUrl: '/app/partials/form.htm',
            controller: 'RegistrationCtrl',
            resolve: {
            	domain: function(loader){
            		return loader.loadDomain();
            	}
            }
        })
        
        // nested states 
        // each of these sections will have their own view
        // url will be nested (/form/details)
        .state('form.details', {
            url: '/details',
            templateUrl: '/app/partials/form-details.htm'
        })
        
        // url will be /form/interests
        .state('form.verification', {
            url: '/validation',
            templateUrl: '/app/partials/form-validation.htm'
        })
        
        // url will be /form/payment
        .state('form.success', {
            url: '/success',
            templateUrl: '/app/partials/form-success.htm'
        });
        
    // catch all route
    // send users to the form page 
    $urlRouterProvider.otherwise('/form/details');
})