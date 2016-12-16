function base64ArrayBuffer(arrayBuffer) {
  var base64    = ''
  var encodings = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/'

  var bytes         = new Uint8Array(arrayBuffer)
  var byteLength    = bytes.byteLength
  var byteRemainder = byteLength % 3
  var mainLength    = byteLength - byteRemainder

  var a, b, c, d
  var chunk

  // Main loop deals with bytes in chunks of 3
  for (var i = 0; i < mainLength; i = i + 3) {
    // Combine the three bytes into a single integer
    chunk = (bytes[i] << 16) | (bytes[i + 1] << 8) | bytes[i + 2]

    // Use bitmasks to extract 6-bit segments from the triplet
    a = (chunk & 16515072) >> 18 // 16515072 = (2^6 - 1) << 18
    b = (chunk & 258048)   >> 12 // 258048   = (2^6 - 1) << 12
    c = (chunk & 4032)     >>  6 // 4032     = (2^6 - 1) << 6
    d = chunk & 63               // 63       = 2^6 - 1

    // Convert the raw binary segments to the appropriate ASCII encoding
    base64 += encodings[a] + encodings[b] + encodings[c] + encodings[d]
  }

  // Deal with the remaining bytes and padding
  if (byteRemainder == 1) {
    chunk = bytes[mainLength]

    a = (chunk & 252) >> 2 // 252 = (2^6 - 1) << 2

    // Set the 4 least significant bits to zero
    b = (chunk & 3)   << 4 // 3   = 2^2 - 1

    base64 += encodings[a] + encodings[b] + '=='
  } else if (byteRemainder == 2) {
    chunk = (bytes[mainLength] << 8) | bytes[mainLength + 1]

    a = (chunk & 64512) >> 10 // 64512 = (2^6 - 1) << 10
    b = (chunk & 1008)  >>  4 // 1008  = (2^6 - 1) << 4

    // Set the 2 least significant bits to zero
    c = (chunk & 15)    <<  2 // 15    = 2^4 - 1

    base64 += encodings[a] + encodings[b] + encodings[c] + '='
  }

  return base64
}

function getFileNameFromHttpResponse(httpResponse) {
    var contentDispositionHeader = httpResponse.headers('Content-Disposition');
    var result = contentDispositionHeader.split(';')[1].trim().split('=')[1];
    return result.replace(/"/g, '');
}

angular.module('app')

.directive("fileResource",function(globalConfig){
    return {
        restrict: "AE",
        templateUrl: globalConfig.basePath + "/app/partials/templates/file-field.htm",
        scope:{
            viewUrl: "@viewUrl",
            downloadUrl: "@downloadUrl"
        },
        controller: function($scope, $mdDialog, $timeout, globalConfig, umaas){
            $scope.view = function($event){
                var parentEl = angular.element(document.body);
                
                $mdDialog.show({
                    parent: parentEl,
                    targetEvent: $event,
                    templateUrl: globalConfig.basePath + '/app/partials/templates/resource.htm',
                    controller: function($scope, $sce,
                    		viewUrl, downloadUrl, $http){
                    	var auth, code;
                    	code = umaas.getAccessCode();
                    	auth = 'Basic ' + btoa( code.id + ":" +code.code);
                    	$scope.noContent = false;
                        var getTrustedUrl = function(url){
                                return $sce.trustAsResourceUrl(url);
                            };
                          $scope.url =  getTrustedUrl(viewUrl);
                          $scope.ok = function () {
                            $mdDialog.hide('ok');
                          }
                          $scope.iframeId = viewUrl.split("/").join("");
                          var data;
                          var fileName;
                          var contentType;
                          $scope.initialize = function(){
                        	  console.log("Sending ajax request");
                        	  console.log($scope.url);
                        	  console.log(downloadUrl);
                        	  $http.get(downloadUrl, {
                        		  responseType: "arraybuffer",
                                  headers: {'Content-Type': undefined, 'Authorization': auth}
                              }).then(function(resp){
                            	  if(resp.status === 204){
                            		  $scope.noContent = true;
                            		  return;
                            	  }
                            	  console.log(resp);
                            	  data = resp.data;
                            	  fileName = getFileNameFromHttpResponse(resp);
                            	  console.log(fileName);
                            	  contentType =  resp.headers("Content-Type");
                            	  var intro = "data:" + contentType;
                            	  console.log(intro);
                  		          $("#" + $scope.iframeId).attr('src',intro+ ";base64," + base64ArrayBuffer(data));   
                                  return;
                              },function(err){
                                  alert(err);
                                  return;
                              });
                        	 
                          }
                          $scope.download = function(){
                        	  download( data, fileName, contentType );
                          }
                    },
                    locals: { 
                        viewUrl: $scope.viewUrl,
                        downloadUrl: $scope.downloadUrl
                    }

                }).then(function (ret) {
                   console.log(ret, "dismissed");

                  });
            };
        }
    };
});