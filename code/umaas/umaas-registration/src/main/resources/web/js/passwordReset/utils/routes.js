angular.module('passwordReset')

.config(function($stateProvider, $urlRouterProvider, globalConfig) {
    
    $stateProvider
    
        // route to show our basic form (/form)
        .state('passwordReset', {
            url: '/passwordReset',
            templateUrl: globalConfig.basePath + '/app/partials/passwordReset/index.htm',
            controller: 'PasswordResetCtrl',
            abstract: true,
            resolve: {
            	domain: function(loader){
            		return loader.loadDomain();
            	}
            }
        })
        
        // nested states 
        // each of these sections will have their own view
        // url will be nested (/form/details)
        .state('passwordReset.enterEmail', {
            url: '/enterEmail',
            templateUrl: globalConfig.basePath + '/app/partials/passwordReset/enter-email.htm'
        })
         .state('passwordReset.emailSent', {
            url: '/emailSent',
            templateUrl: globalConfig.basePath + '/app/partials/passwordReset/email-sent.htm'
        })
        
        // url will be /form/interests
        .state('passwordReset.changePassword', {
            url: '/changePassword/:userId?tokenId&code}',
            templateUrl: globalConfig.basePath + '/app/partials/passwordReset/change-password.htm',
            controller: 'PasswordChangeCtrl'
        })
        
        // url will be /form/payment
        .state('passwordReset.success', {
            url: '/success',
            templateUrl: globalConfig.basePath + '/app/partials/passwordReset/success.htm'
        });
        
    // catch all route
    // send users to the form page 
    $urlRouterProvider.otherwise('/passwordReset/enterEmail');
})