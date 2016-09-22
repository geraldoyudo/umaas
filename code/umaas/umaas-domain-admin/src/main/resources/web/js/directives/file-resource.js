angular.module('app')

.directive("fileResource",function(){
    return {
        restrict: "AE",
        templateUrl: "/app/partials/templates/file-field.htm",
        scope:{
            viewUrl: "@viewUrl",
            downloadUrl: "@downloadUrl"
        },
        controller: function($scope, $mdDialog, $timeout){
            $scope.view = function($event){
                var parentEl = angular.element(document.body);
                
                $mdDialog.show({
                    parent: parentEl,
                    targetEvent: $event,
                    templateUrl: '/app/partials/templates/resource.htm',
                    controller: function($scope, $sce, viewUrl){
                        var getTrustedUrl = function(url){
                                return $sce.trustAsResourceUrl(url);
                            };
                          $scope.url =  getTrustedUrl(viewUrl);
                          $scope.ok = function () {
                            $mdDialog.hide('ok');
                          }
                    },
                    locals: { 
                        viewUrl: $scope.viewUrl
                    }

                }).then(function (ret) {
                   console.log(ret, "dismissed");

                  });
            };
        }
    };
});