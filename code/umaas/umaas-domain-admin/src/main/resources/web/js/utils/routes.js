angular.module('app')

.config(function($stateProvider, $urlRouterProvider) {
    
    $stateProvider
    
        // route to show our basic form (/form)
        .state('home', {
            url: '/'
        })
        
        
    // catch all route
    // send users to the form page 
    $urlRouterProvider.otherwise('/');
})