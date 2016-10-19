var traversonRef;
try{
    if(!window){
    //console.log("not in browser");
    traversonRef = require('traverson');
    TraversonJsonHalAdapterRef = require('traverson-hal');
  }else{
    //console.log("in browser")
    traversonRef = traverson;
    TraversonJsonHalAdapterRef = TraversonJsonHalAdapter;
  }
}catch(e){
   traversonRef = require('traverson');
   TraversonJsonHalAdapterRef = require('traverson-hal');
}

traversonRef.registerMediaType(TraversonJsonHalAdapterRef.mediaType,
    TraversonJsonHalAdapterRef);

var api = traversonRef.newRequest().jsonHal().withRequestOptions({ headers: { 'Authorization': 'Basic MDAwMDowMDAw' }});
var utils = {};
var apiBaseUrl = "http://localhost:8090";
var currentDomain ;
var code = {id:"0000", code:"0000"};

utils.updateObject = function (url, object, callback){
    //console.log("Updating");
   api.newRequest().from(url)
      .addRequestOptions({headers:{'Content-Type': 'application/json'}})
      .patch(object, function(error, response, traversal){
    	if(error) return callback(error);
        var body;
        if( response){
          body = response.body;
        }
        if((body === undefined || body === '')  && response.headers && response.headers.location){
          var location = response.headers.location;
          api.newRequest().from(location)
            .getResource(function(error, resource, traversal){
              callback(error, resource,traversal);
            })
        }else{
           //console.log(response);
          //console.log(traversal);
           callback(error, JSON.parse(body), traversal); 	 
         
        } 
      });
}
utils.deleteObject = function(url, callback){
    //console.log("deleting from " + url);
    api.newRequest().from(url)
      .delete(function(error, response, traversal){
      	if(error) return callback(error);
        var body;
        if( response){
          body = response.body;
        }
        //console.log(response);
        //console.log(traversal);
        callback(error, {}, traversal);
      });
}
utils.insertObject = function(url, object, callback){
    //console.log("inserting to " + url);
    //console.log(object);
    api.newRequest().from(url)
      .addRequestOptions({headers:{'Content-Type': 'application/json'}})
      .post(object, function(error, response, traversal){
      	if(error) return callback(error);
        var body;
        if( response){
          body = response.body;
        }
         if((body === undefined || body === '') && response.headers && response.headers.location){
          var location = response.headers.location;
          api.newRequest().from(location)
            .getResource(function(error, resource, traversal){
              callback(error, resource,traversal);
            })
        }else{
           //console.log(response);
          //console.log(traversal);
          callback(error, JSON.parse(body), traversal);
        } 
      });
}
utils.getObject = function(url, callback){
    //console.log("inserting to " + url);
    //console.log(object);
    api.newRequest().from(url)
      .addRequestOptions({headers:{'Content-Type': 'application/json'}})
      .getResource(function(error, resource, traversal){
      	  if(error) return callback(error);
           callback(undefined, resource, traversal);
      });
}

var setDomain = function(domain){
  currentDomain = domain ;
}
var getDomain = function(){
	  return currentDomain;
}

var setBaseUrl = function(baseUrl){
  apiBaseUrl = baseUrl;
}

var getBaseUrl = function(baseUrl){
	return apiBaseUrl;
}
var setAccessCode = function(accessCode){
 code = accessCode;
  if(accessCode){
    //console.log("Setting accessCode");
    var auth =  '';
    auth = accessCode.id + ":" + accessCode.code;
    auth = btoa(auth);
    auth = 'Basic ' + auth;
   api = traversonRef.newRequest().jsonHal().withRequestOptions({ headers: { 'Authorization': auth }});
  }

}

var getAccessCode = function(){
  return code;
}
// Lazy List
// lazy list template
var LazyList = function(resourceObject, Creator, collectionName, traversal, callback){
  var size = 0;
  var totalElements = 0;
  var totalPages =0;
  var pageNumber = 0;
  var resource = resourceObject;
  this.content =  [];
  var resourceLength = 0;
  var content = this.content;
  var resourceContent = [];
  try{
  resourceContent = resourceObject._embedded[collectionName];

  size = resource.page.size;
  totalElements = resource.page.totalElements;
  totalPages = resource.page.totalPages;
  pageNumber = resource.page.number;
  resourceLength = resourceContent.length;
  }catch(e){
	  // do nothing
  }
  var counter = 0;
  this.getSize = function(){
    return size;
  }
  this.getTotalElements = function(){
    return totalElements;
  }
  this.getTotalPages = function(){
    return totalPages;
  }
  this.getPageNumber = function(){
    return  pageNumber;
  }
  this.next = function(callback){
     // navigate to next resource
      if(!this.hasNext()) return callback();
     var url = resource._links.next.href;
     api.newRequest().from(url).getResource(function(error,response){
          if(!error){
            return callback(undefined, new LazyList(response,Creator, collectionName, traversal, callback));
          }else{
            return callback(error, undefined);
          }
     });
   };
   this.previous = function(callback){
    if(!this.hasPrevious()) return callback();
     var url = resource._links.prev.href;
     api.newRequest().from(url).getResource(function(error,response){
          if(!error){
            return callback(undefined, new LazyList(response,Creator, collectionName, traversal, callback));
          }else{
            return callback(error, undefined);
          }
     });
   }
   this. hasNext = function(){
      return (pageNumber + 1) != totalPages;
   }
   this.hasPrevious =  function(){
     return pageNumber > 0;
   }
  if(resourceLength > 0){
	  resourceContent.forEach(function(element) {
	     if(!traversal)
	        content.push(new Creator(element));
	     else{
	       var traversalUrl;
	       if(typeof traversal === 'string'){
	        var traversalLink = element._links[traversal];
	        if(!traversalLink) return;
	        traversalUrl = traversalLink.href;
	       }else{
	         traversalUrl = traversal(element);
	       }
	       utils.getObject(traversalUrl, function(error, resource){
	         content.push(new Creator(resource));
	         counter++;
	         if(counter == resourceLength){
	        	 if(callback) return callback();
	         }
	       })
	     }
	  });
  }else{
 	 if(callback) return callback();
  }
  

}

/*
 * Object constructors
 */
var initResource = function(resource, resourceObject){
  if(resourceObject){
    resource.id = resourceObject._links.self.href.split('/').pop() || '';
    resource.meta = resourceObject.meta;
    resource.externalId = resourceObject.externalId;
    resource.url = resourceObject._links.self.href || '';
  }else{
    resource.id = '';
    resource.meta = '';
    resource.externalId = '';
    resource.url = '';
  }

}
var Domain =  function(resourceObject){
      this.url = '';
      this.id = '';
      this.code =  '';
      this.name = '';
      this.properties = {};
      if(resourceObject){
        initResource(this, resourceObject);
        this.code = resourceObject.code || '';
        this.name = resourceObject.name || '';
        this.properties = resourceObject.properties || {};
      }
      this.sync = function(callback){
        if(!this.id) callback({message: "not saved yet"});
        var domainObject = {};
        domainObject.id = this.id;
        domainObject.externalId = this.externalId;
        domainObject.code = this.code;
        domainObject.meta = this.meta;
        domainObject.name = this.name;
        domainObject.properties = this.properties;
        //console.log({message:'this is where we want.'});
        //console.log(domainObject);
        this.update(domainObject,callback);
      }

      this.load = function(callback){
        if(!this.id) callback({message: "not saved yet"});
        var self = this;
         umaas.domains.findById(this.id, function(error, domain){
            self.id = domain.id;
            self.externalId = domain.externalId;
            self.meta = domain.meta;
            self.code = domain.code;
            self.name = domain.name;
            self.properties = domain.properties;
            callback();
         })
      }
      this.update = function(domainObject, callback){
          utils.updateObject(this.url, domainObject, function(error, response, traversal){
              if(error){
                callback(error);
              }else{
                callback(undefined,new Domain(response));
              }

          });
      }
      this.insert = function(callback){
          var domainObject = {};
            domainObject.code = this.code;
            domainObject.name = this.name;
            domainObject.properties = this.properties;
            utils.insertObject(apiBaseUrl + '/domain/domains', domainObject, function(error, response, traversal){
              if(error){
                callback(error);
              }else{
                callback(undefined,new Domain(response));
              }

            });
        };
     this.delete = function(callback){
          utils.deleteObject(this.url, function(error, response, traversal){
            if(error){
              callback(error);
            }else{
              callback();
            }
          })
     };
}

var AppUser =  function(resourceObject){
      this.url = '';
      this.id = '';
      this.username =  '';
      this.password = '';
      this.email = '';
      this.phoneNumber = '';
      this.phoneNumberVerified = false;
      this.emailVerified = false;
      this.domain = '';
      this.properties = {};
      this.groups = [];
      this.roles = [];
      this.disabled = false;
      this.credentialsExpired = false;
      this.locked = false;
      var domain = '';

      if(resourceObject){
        initResource(this, resourceObject);
        try{
        this.phoneNumber = resourceObject.phoneNumber || '' ;
        this.email = resourceObject.email || '';
        this.username = resourceObject.username || '';
        this.password = resourceObject.password || '';
        this.phoneNumberVerified = resourceObject.phoneNumberVerified || false ;
        this.emailVerified = resourceObject.emailVerified || false;
        this.groups = resourceObject.groups || [];
        this.roles = resourceObject.roles || [];
        this.properties = resourceObject.properties || {};
        this.disabled = resourceObject.disabled;
        this.credentialsExpired = resourceObject.credentialsExpired;
        this.locked = resourceObject.locked;
        domain =  resourceObject._links.domain.href;
        } catch(e){};
      }




      this.sync = function(callback){
        if(!this.id) callback({message: "not saved yet"});
        var userObject = {};
        userObject.id = this.id;
        userObject.phoneNumber = this.phoneNumber;
        userObject.emailVerified = this.emailVerified;
        userObject.password = this.password;
        userObject.email = this.email;
        userObject.phoneNumberVerified = this.phoneNumberVerified;
        userObject.groups = this.groups;
        userObject.roles = this.roles;
        userObject.username = this.username;
        userObject.properties = this.properties;
        userObject.externalId = this.externalId;
        userObject.meta = this.meta;
        userObject.disabled = this.disabled;
        userObject.credentialsExpired = this.credentialsExpired;
        userObject.locked = this.locked;
        //console.log({message:'this is where we want.'});
        //console.log(userObject);
        this.update(userObject,callback);
      }

      this.load = function(callback){
        if(!this.id) callback({message: "not saved yet"});
        var self = this;
         umaas.appUsers.findById(this.id, function(error, appUsers){
           self.id = appUsers.id;
           self.phoneNumber = appUsers.phoneNumber;
           self.emailVerified = appUsers.emailVerified;
           self.password = appUsers.password;
           self.email = appUsers.email;
           self.phoneNumberVerified = appUsers.phoneNumberVerified;
           self.groups = appUsers.groups;
           self.roles = appUsers.roles;
           self.username = appUsers.username;
           self.disabled = appUsers.disabled;
           self.credentialsExpired = appUsers.credentialsExpired;
           self.locked = appUsers.locked;
           self.properties = appUsers.properties;
            callback();
         })
      }


      this.update = function(userObject, callback){
          utils.updateObject(this.url, userObject, function(error, response, traversal){
              if(error){
                callback(error);
              }else{
                callback(undefined,new AppUser(response));
              }

          });
      }
      this.insert = function(callback, domainId){
        if (domainId === undefined && currentDomain){
          domainId = currentDomain.id ;
        }

        if (domainId === undefined){
          return callback({message:"domain not specified"});

        }
          var userObject = {};
            userObject.email = this.email;
            userObject.password = this.password;
            userObject.groups = this.groups;
            userObject.domain = 'domain/domains/' + domainId;
            userObject.phoneNumber = this.phoneNumber;
            userObject.username = this.username;
            userObject.properties = this.properties;
            userObject.disabled = this.disabled;
            userObject.credentialsExpired = this.credentialsExpired;
            userObject.locked = this.locked;
            utils.insertObject(apiBaseUrl + '/domain/appUsers', userObject, function(error, response, traversal){
              if(error){
                callback(error);
              }else{
                callback(undefined,new AppUser(response));
              }

            });
        };
     this.delete = function(callback){
          utils.deleteObject(this.url, function(error, response, traversal){
            if(error){
              callback(error);
            }else{
              callback();
            }
          })
     };

     this.getDomain = function(callback){
       //console.log('Domain of user');
       //console.log(domain);
       if (domain !== undefined && domain !== ''){
         api.newRequest().from(domain)
            .getResource(function(error, response, traversal){
              if(error)  return callback(error);
              //console.log(response);
              //console.log(traversal);
              var dom  = new Domain(response);
              //console.log(dom);
              return callback(error, dom);
            });
       }else {
        return callback();
       }
     }

     this.set = function(fieldname, value){
       propreties[fieldname] = value ;
     }

     this.get = function(fieldname){
       return properties[fieldname] ;
     }

     this.setFileProperty = function(fieldName, blob, callback){
       var userId = this.id;
        umaas.fields.findByName(fieldName, function(error, field){
            if(error) return callback(error);
            field.setFileProperty(userId, blob, callback);
        })
     }

      this.getFileProperty = function(fieldName, callback){
       var userId = this.id;
        umaas.fields.findByName(fieldName, function(error, field){
            if(error) return callback(error);
            field.getFileProperty(userId, callback);
        })
     }
      
      this.getFileUrl = function(fieldName, callback, view){
    	  var userId = this.id;
          umaas.fields.findByName(fieldName, function(error, field){
              if(error) return callback(error);
              return callback(apiBaseUrl + '/files/user/'+ (view?'view':'download') + '/' + userId + "/" + fieldId) ;
          })
      }
      this.getFileUrlByFieldId = function(fieldId, view){
    	  var userId = this.id;
    	 return apiBaseUrl + '/files/user/'+ (view?'view':'download') + '/' + userId + "/" + fieldId;
      }
}


var Group = function(resourceObject){
      initResource(this, resourceObject);
      this.name =  '';
      var roles = [];
      var domain = '';
      var parentUrl = '';

      if(resourceObject){
        try{ 
          initResource(this, resourceObject);
          this.name = resourceObject.name || '' ;
          this.roles = resourceObject.roles || [];
          domain =  resourceObject._links.domain.href;
          parentUrl = resourceObject._links.parent.href;
        } catch(e){}
      }




      this.sync = function(callback){
        if(!this.id) callback({message: "not saved yet"});
        var groupObject = {};
        groupObject.id = this.id;
        groupObject.name = this.name;
        groupObject.roles = this.roles;
        groupObject.parentUrl = this.parentUrl;
        groupObject.domain = this.domain;
        groupObject.externalId = this.externalId;
        groupObject.meta = this.meta;
        //console.log({message:'this is where we want.'});
        //console.log(groupObject);
        this.update(groupObject,callback);
      }

      this.load = function(callback){
        if(!this.id) callback({message: "not saved yet"});
        var self = this;
         umaas.groups.findById(this.id, function(error, groups){
           self.id = groups.id;
           self.name = groups.name;
           self.parentUrl = groups.parentUrl;
           self.roles = groups.roles;
           self.domain = groups.domain;
           callback();
         })
      }




       this.update = function(groupObject, callback){
          utils.updateObject(this.url, groupObject, function(error, response, traversal){
              if(error){
                callback(error);
              }else{
                callback(undefined,new Group(response));
              }

          });
      }

      this.insert = function(callback, domainId){
        if (domainId === undefined && currentDomain){
          domainId = currentDomain.id ;
        }

        if (domainId === undefined){
          return callback({message:"domain not specified"});

        }
          var groupObject = {};
            groupObject.name = this.name;
            groupObject.domain = "/domain/domains/" + domainId;
            groupObject.parent = parentUrl;
            utils.insertObject(apiBaseUrl + '/domain/groups', groupObject, function(error, response, traversal){
              if(error){
                callback(error);
              }else{
                callback(undefined,new Group(response));
              }

            });
        };
     this.delete = function(callback){
          utils.deleteObject(this.url, function(error, response, traversal){
            if(error){
              callback(error);
            }else{
              callback();
            }
          })
     };

      this.getDomain = function(callback){
       if (domain !== undefined && domain !== ''){
         api.newRequest().from(domain)
            .getResource(function(error, response, traversal){
              //console.log(response);
              //console.log(traversal);
              callback(error, new Domain(response), traversal);
            });
       }else {
         callback();
       }
     }

      this.getParent = function(callback){
       if (parentUrl !== undefined && parentUrl !== ''){
         api.newRequest().from(parentUrl)
            .getResource(function(error, response, traversal){
              //console.log(response);
              //console.log(traversal);
              callback(error, new Group(response), traversal);
            });
       }else {
         callback();
       }
     }

     this.setParent = function(group){
       parentUrl = group.url;
     }
     this.addUser = function(userId, callback){
       var groupId = this.id;
        this.getDomain(function(error, domain){
          if(error) callback({message: "cannot access domain"});
            var userGroup = {
              group: "/domain/groups/" + groupId,
              user: "/domain/appUsers/" + userId,
              domain: "/domain/domains/" + domain.id
            };
            //console.log(userGroup);
             utils.insertObject(apiBaseUrl + '/domain/userGroups', userGroup, function(error, response, traversal){
               //console.log(response);
                if(error){
                  callback(error);
                }else{
                  callback();
                }
            });
        })
     }
     this.removeUser = function(userId, callback){
         var url = apiBaseUrl + "/domain/userGroups/search/findByUserIdAndGroupId";
         var groupId = this.id;
        api.newRequest().from(url)
        .addRequestOptions({ qs: { userId: userId, groupId:groupId } })
        .getResource(function(error, resource){
            if(error) callback();
            if(resource){
               //console.log("Found group entity to delete");
              var resourceUrl = resource._links.self.href;
              //console.log(resourceUrl);
              api.newRequest().from(resourceUrl).delete(function(error, response){
                //console.log(response);
                if(error) return callback(error);
                else return callback();
              })
            }else{
              return callback();
            }
        });
     }
     this.getUsers = function(pageObject, callback){
        var url = apiBaseUrl + "/domain/userGroups/search/findByGroupId";
         var groupId = this.id;
         if(!pageObject) pageObject = {};
         pageObject['groupId'] = groupId;
        api.newRequest().from(url)
        .addRequestOptions({ qs: pageObject })
        .getResource(function(error, resource){
            if(error) callback();
            if(resource){
              var userList = new umaas.LazyList(resource, AppUser, "userGroups", "user", function(error){
                    if(error) callback(error);
                    callback(undefined, userList);
              });

            }else{
              return callback();
            }
        });
     }

     this.getUsersNotInGroup = function(pageObject, callback){
        var url = apiBaseUrl + "/domain/userGroups/search/findByGroupIdNotAndDomainId";
         var groupId = this.id;
         if(!pageObject) pageObject = {};
         pageObject['groupId'] = groupId;
         pageObject['domainId'] = umaas.getDomain().id;
        api.newRequest().from(url)
        .addRequestOptions({ qs: pageObject })
        .getResource(function(error, resource){
            if(error) callback();
            if(resource){
              var userList = new umaas.LazyList(resource, AppUser, "userGroups", "user", function(error){
                    if(error) callback(error);
                    callback(undefined, userList);
              });

            }else{
              return callback();
            }
        });
     }
}

var Role = function(resourceObject){
      initResource(this, resourceObject);
      this.name =  '';
      var domain = '';

      if(resourceObject){
        initResource(this, resourceObject);
        this.name = resourceObject.name || '' ;
        domain =  resourceObject._links.domain.href;
      }

      this.sync = function(callback){
        if(!this.id) callback({message: "not saved yet"});
        var roleObject = {};
        roleObject.id = this.id;
        roleObject.name = this.name;
        roleObject.domain = this.domain;
        roleObject.externalId = this.externalId;
        roleObject.meta = this.meta;
        //console.log({message:'this is where we want.'});
        //console.log(roleObject);
        this.update(roleObject,callback);
      }

      this.load = function(callback){
        if(!this.id) callback({message: "not saved yet"});
        var self = this;
         umaas.roles.findById(this.id, function(error, roles){
           self.id = roles.id;
           self.name = roles.name;
           self.domain = roles.domain;
           callback();
         })
      }

       this.update = function(roleObject, callback){
          utils.updateObject(this.url, roleObject, function(error, response, traversal){
              if(error){
                callback(error);
              }else{
                callback(undefined,new Role(response));
              }

          });
      }
      this.insert = function(callback, domainId){
        if (domainId === undefined && currentDomain){
          domainId = currentDomain.id ;
        }

        if (domainId === undefined){
          return callback({message:"domain not specified"});

        }
          var roleObject = {};
            roleObject.name = this.name;
            roleObject.domain = "/domain/domains/" + domainId;
            utils.insertObject(apiBaseUrl + '/domain/roles', roleObject, function(error, response, traversal){
              if(error){
                callback(error);
              }else{
                callback(undefined,new Role(response));
              }

            });
        };
     this.delete = function(callback){
          utils.deleteObject(this.url, function(error, response, traversal){
            if(error){
              callback(error);
            }else{
              callback();
            }
          })
     };

      this.getDomain = function(callback){
       if (domain !== undefined && domain !== ''){
         api.newRequest().from(domain)
            .getResource(function(error, response, traversal){
              //console.log(response);
              //console.log(traversal);
              callback(error, new Domain(response), traversal);
            });
       }else {
         callback();
       }
     }

      this.getParent = function(callback){
       if (parentUrl !== undefined && parentUrl !== ''){
         api.newRequest().from(parentUrl)
            .getResource(function(error, response, traversal){
              //console.log(response);
              //console.log(traversal);
              callback(error, new Group(response), traversal);
            });
       }else {
         callback();
       }
     }

      this.addKey = function(key, callback, roleType){
       var roleId = this.id;
        this.getDomain(function(error, domain){
          if(error) callback({message: "cannot access domain"});
            var roleMap = {
              role: "/domain/roles/" + roleId,
              key: key,
              domain: "/domain/domains/" + domain.id,
              type: roleType || 'USER'
            };
            //console.log(roleMap);
             utils.insertObject(apiBaseUrl + '/domain/roleMappings', roleMap, function(error, response, traversal){
               //console.log(response);
                if(error){
                  callback(error);
                }else{
                  callback();
                }
            });
        })
     }
     this.removeKey = function(key, callback){
         var url = apiBaseUrl + "/domain/roleMappings/search/findByKeyAndRoleId";
         var roleId = this.id;
        api.newRequest().from(url)
        .addRequestOptions({ qs: { key: key, roleId:roleId } })
        .getResource(function(error, resource){
            if(error) callback();
            if(resource){
               //console.log("Found role entity to delete");
              var resourceUrl = resource._links.self.href;
              //console.log(resourceUrl);
              api.newRequest().from(resourceUrl).delete(function(error, response){
                //console.log(response);
                if(error) return callback(error);
                else return callback();
              })
            }else{
              return callback();
            }
        });
     }
     var urlConverter = function(element, name){
       return umaas.getBaseUrl() + '/domain/' + name + '/' + element.key;
     }

     this.getWithCriteria = function(pageObject, type, isIn, callback){
         var url = apiBaseUrl + "/domain/roleMappings/search/findByDomainIdAndTypeAndRoleId" + ((!isIn)? 'Not' : '');
         var roleId = this.id;
         if(!pageObject) pageObject = {};
         pageObject['roleId'] = roleId;
         pageObject['type'] = type
         pageObject['domain'] = umaas.getDomain().id;
        api.newRequest().from(url)
        .addRequestOptions({ qs: pageObject })
        .getResource(function(error, resource){
            if(error) callback();
            if(resource){
              var userList = new umaas.LazyList(resource, (type === 'GROUP'? Group: AppUser), "roleMappings",
               function(element){
            	  console.log('converting element');
            	  console.log(element);
                 return urlConverter(element, (type === 'GROUP'? 'groups': 'appUsers'));
              }, function(error){
                    if(error) callback(error);
                    callback(undefined, userList);
              });

            }else{
              return callback();
            }
        });
     }
}


var AccessCode = function(resourceObject){
      initResource(this, resourceObject);
      this.code =  '';
      this.expiryDate = '';
      this.enabled = false;
      this.notLocked = false;
      this.notExpired = false;

      if(resourceObject){
        initResource(this, resourceObject);
        this.code = resourceObject.code || '' ;
        this.expiryDate = resourceObject.expiryDate;
        this.enabled = resourceObject.enabled;
        this.notLocked = resourceObject.notLocked;
        this.notExpired = resourceObject.notExpired;

      }


      this.sync = function(callback){
        if(!this.id) callback({message: "not saved yet"});
        var accessCodeObject = {};
        accessCodeObject.id = this.id;
        accessCodeObject.code = this.code;
        accessCodeObject.domain = this.domain;
        accessCodeObject.externalId = this.externalId;
        accessCodeObject.meta = this.meta;
        accessCodeObject.expiryDate = this.expiryDate;
        accessCodeObject.enabled = this.enabled;
        accessCodeObject.notLocked = this.notLocked;
        accessCodeObject.notExpired = this.notExpired;
        //console.log({message:'this is where we want.'});
        //console.log(accessCodeObject);
        this.update(accessCodeObject,callback);
      }

      this.load = function(callback){
        if(!this.id) callback({message: "not saved yet"});
        var self = this;
         umaas.accessCodes.findById(this.id, function(error, accessCodes){
           self.id = accessCodes.id;
           self.code = accessCodes.code;

           self.domain = accessCodes.domain;
           callback();
         })
      }

       this.update = function(codeObject, callback){
          utils.updateObject(this.url, codeObject, function(error, response, traversal){
              if(error){
                callback(error);
              }else{
                callback(undefined,new AccessCode(response));
              }

          });
      }
      this.insert = function(callback){
          var codeObject = {};
            codeObject.code = this.code;
            utils.insertObject(apiBaseUrl + '/domain/domainAccessCodes', codeObject, function(error, response, traversal){
              if(error){
                callback(error);
              }else{
                callback(undefined,new AccessCode(response));
              }

            });
        };
     this.delete = function(callback){
          utils.deleteObject(this.url, function(error, response, traversal){
            if(error){
              callback(error);
            }else{
              callback();
            }
          })
     };
}



var Field = function(resourceObject){
  initResource(this, resourceObject);
  this.name =  '';
  this.properties = {};
  this.mandatory = false;
  this.registrationItem = false;
  this.type = 'string';
  var domain = '';


  if(resourceObject){
    initResource(this, resourceObject);
    this.name = resourceObject.name || '' ;
    this.properties = resourceObject.properties || {};
    domain =  resourceObject._links.domain.href;
    this.registrationItem = resourceObject.registrationItem;
    this.mandatory = resourceObject.mandatory;
    this.type = resourceObject.type;
  }



  this.sync = function(callback){
    if(!this.id) callback({message: "not saved yet"});
    var fieldObject = {};
    fieldObject.id = this.id;
    fieldObject.name = this.name;
    fieldObject.domain = this.domain;
    fieldObject.mandatory = this.mandatory;
    fieldObject.registrationItem = this.registrationItem;
    fieldObject.type = this.type;
    fieldObject.properties = this.properties;
    fieldObject.externalId = this.externalId;
    fieldObject.meta = this.meta;
    //console.log({message:'this is where we want.'});
    //console.log(fieldObject);
    this.update(fieldObject,callback);
  }

  this.load = function(callback){
    if(!this.id) callback({message: "not saved yet"});
    var self = this;
     umaas.fields.findById(this.id, function(error, fields){
       self.id = fields.id;
       self.name = fields.name;
       self.domain = fields.domain;
       self.properties = fields.properties;
       callback();
     })
  }



  this.update = function(fieldObject, callback){
     utils.updateObject(this.url, fieldObject, function(error, response, traversal){
         if(error){
           callback(error);
         }else{
           callback(undefined,new Field(response));
         }

     });
 }


 this.insert = function(callback, domainId){
   if (domainId === undefined && currentDomain){
     domainId = currentDomain.id ;
   }

   if (domainId === undefined){
     return callback({message:"domain not specified"});

   }
     var fieldObject = {};
       fieldObject.name = this.name;
       fieldObject.type = this.type;
       fieldObject.mandatory = this.mandatory;
       fieldObject.registrationItem = this.registrationItem;
       fieldObject.properties = this.properties;
       fieldObject.domain = "/domain/fields/" + domainId;
       utils.insertObject(apiBaseUrl + '/domain/fields', fieldObject, function(error, response, traversal){
         if(error){
           callback(error);
         }else{
           callback(undefined,new Field(response));
         }

       });
   };
this.delete = function(callback){
     utils.deleteObject(this.url, function(error, response, traversal){
       if(error){
         callback(error);
       }else{
         callback();
       }
     })
};


this.set = function(fieldname, value){
  this.propreties[fieldname] = value ;
}

this.get = function(fieldname){
  return this.properties[fieldname] ;
}

this.getDomain = function(callback){
  //console.log('Domain of field');
  //console.log(domain);
  if (domain !== undefined && domain !== ''){
    api.newRequest().from(domain)
       .getResource(function(error, response, traversal){
         if(error)  return callback(error);
         //console.log(response);
         //console.log(traversal);
         var dom  = new Domain(response);
         //console.log(dom);
         return callback(error, dom);
       });
  }else {
   return callback();
  }
}

this.setFileProperty = function(userId, blob, callback){
  var url, fieldId, auth;
  var data = new FormData();
  data.append("file", blob);
  fieldId = this.id ;
  url = apiBaseUrl + '/files/user/upload/' + userId + "/" + fieldId ;
	auth = 'Basic ' + btoa( code.id + ":" +code.code)
  jQuery.ajax({
    url: url,
    data: data,
    cache: false,
    contentType: false,
    processData: false,
    type: 'POST',
    beforeSend: function(xhr){xhr.setRequestHeader('Authorization', auth)},
    success: function(data){
      callback(undefined,data);
    },
    error: function(error){
      callback(error);
    }

  })

}


this.getFileProperty = function(userId, callback){
  var url, fieldId, auth;
  fieldId = this.id ;
  url = apiBaseUrl + '/files/user/view/' + userId + "/" + fieldId ;
	auth = 'Basic ' + btoa( code.id + ":" +code.code)
  jQuery.ajax({
    url: url,
    cache: false,
    type: 'GET',
    beforeSend: function(xhr){xhr.setRequestHeader('Authorization', auth)},
    success: function(data){
      //console.log( "download data");
      //console.log(data);
      callback(undefined,data);
      },
    error: function(error){
      //console.log(error);
      callback(error);
     }
  });

}



}

// Entity Managers
var domains = function(){

  this.findById = function(id, callback){
    var url = apiBaseUrl + "/domain/domains/" + id;
    api.newRequest().from(url).getResource(function(error, resource){
        if(error)
          return callback(error);
        else
          return callback(undefined, new Domain(resource));
    });
  }

  this.findAll = function(options, callback){
    var url = apiBaseUrl + '/domain/domains';
     api.newRequest().from(url)
     .addRequestOptions({qs:(options || {})})
     .getResource(function(error, resource){
        if(error)
          return callback(error);
        else
          return callback(undefined, new LazyList(resource,Domain, "domains"));
     });
  }

  this.findByName = function(domainName, callback){
    var url = apiBaseUrl + "/domain/domains/search/findByName";

    api.newRequest().from(url)
    .addRequestOptions({ qs: { name: domainName } })
    .getResource(function(error, resource){
        if(error)
          return callback(error);
        else
          return callback(undefined, new Domain(resource));
    });
  }

  this.findByCode = function(domainCode, callback){
    var url = apiBaseUrl + "/domain/domains/search/findByCode";

    api.newRequest().from(url)
    .addRequestOptions({ qs: { code: domainCode } })
    .getResource(function(error, resource){
        if(error)
          return callback(error);
        else
          return callback(undefined, new Domain(resource));
    });
  }

}

var appUsers = function(){

  this.findById = function(id, callback){
    var url = apiBaseUrl + "/domain/appUsers/" + id;
    api.newRequest().from(url).getResource(function(error, resource){
        if(error)
          return callback(error);
        else
          return callback(undefined, new AppUser(resource));
    });
  }

  this.findAll = function(options, callback){
    var url = apiBaseUrl + '/domain/appUsers';
     api.newRequest().from(url)
     .addRequestOptions({qs:(options || {})})
     .getResource(function(error, resource){
        if(error)
          return callback(error);
        else
          return callback(undefined, new LazyList(resource,AppUser, "appUsers"));
     });
  }

  this.findByPhoneNumber = function(phoneNum, callback){
    var url = apiBaseUrl + '/domain/appUsers/search/findByPhoneNumberAndDomain';
    //console.log(phoneNum);
    //console.log(currentDomain.id);
    if (currentDomain === undefined || currentDomain.id === undefined){
      //console.log("No current domain set" );
      callback({message:"currentDomain is not set"});
    }else{
      api.newRequest().from(url)
      .addRequestOptions({qs:{phoneNumber : phoneNum , domain : currentDomain.id }})
      .getResource(function(error, resource){
         if(error)
           return callback(error);
         else
           return callback(undefined, new AppUser(resource));
      });
    }

  }

  this.findByDomain = function(options, id, callback){
    var url = apiBaseUrl + "/domain/appUsers/search/findByDomain";
    options = options || {};
    options.domain = id;
    api.newRequest().from(url)
      .addRequestOptions({qs:options})
      .getResource(function(error, resource){
        if(error)
          return callback(error);
        else
          return callback(undefined, new LazyList(resource,AppUser, "appUsers"));
    });
  }

  this.findByEmail = function(email, callback){
  var url = apiBaseUrl + "/domain/appUsers/search/findByEmailAndDomain";
  //console.log(email);
  //console.log(currentDomain.id);
  if (currentDomain === undefined || currentDomain.id === undefined){
    callback({message:"currentDomain is not set"});
  }
  api.newRequest().from(url)
    .addRequestOptions({qs:{ email : email, domain : currentDomain.id }})
    .getResource(function(error, resource){
      if(error)
        return callback(error);
      else
        return callback(undefined, new AppUser(resource)) ;
  });
}


this.findByUsername = function(userName, callback){
var url = apiBaseUrl + "/domain/appUsers/search/findByUsernameAndDomain";
//console.log(userName);
//console.log(currentDomain.id);
if (currentDomain === undefined || currentDomain.id === undefined){
  callback({message:"currentDomain is not set"});
}
api.newRequest().from(url)
  .addRequestOptions({qs:{ username : userName, domain : currentDomain.id }})
  .getResource(function(error, resource){
    if(error)
      return callback(error);
    else
      return callback(undefined, new AppUser(resource)) ;
});
}

  this.find = function(options,callback){
      if(currentDomain && currentDomain.id){
        return this.findByDomain(options, currentDomain.id,callback);
      }else{
        callback();
      }
  }
}


var roles = function(){
  this.findAll = function(options, callback){
    var url = apiBaseUrl + '/domain/roles';
     api.newRequest().from(url)
     .addRequestOptions({qs:(options || {})})
     .getResource(function(error, resource){
        if(error)
          return callback(error);
        else
          return callback(undefined, new LazyList(resource,Role, "roles"));
     });
  }

  this.findById = function(id, callback){
    var url = apiBaseUrl + "/domain/roles/" + id;
    api.newRequest().from(url).getResource(function(error, resource){
        if(error)
          return callback(error);
        else
          return callback(undefined, new Role(resource));
    });
  }

  this.findByDomain = function(options,id, callback){
    var url = apiBaseUrl + "/domain/roles/search/findByDomain";
    options = options || {};
    options.domain = id;
    api.newRequest().from(url)
      .addRequestOptions({qs:options})
      .getResource(function(error, resource){
        if(error)
          return callback(error);
        else
          return callback(undefined, new LazyList(resource,Role, "roles"));
    });
  }

  this.findByName = function(name, callback){
    var url = apiBaseUrl + '/domain/roles/search/findByDomainIdAndName';
    //console.log(name);
    //console.log(currentDomain.id);
    if (currentDomain === undefined || currentDomain.id === undefined){
      //console.log("No current domain set" );
      callback({message:"currentDomain is not set"});
    }else{
      //console.log("DOMINA");
      //console.log(currentDomain.id);
      //console.log("NAME");
      //console.log(name);
      api.newRequest().from(url)

      .addRequestOptions({qs:{ domain : currentDomain.id , name : name }})
      .getResource(function(error, resource){
         if(error)
           return callback(error);
         else
           return callback(undefined, new Role(resource));
      });
    }

  }
  
  
  this.find = function(options,callback){
      if(currentDomain && currentDomain.id){
        return this.findByDomain(options, currentDomain.id,callback);
      }else{
        callback();
      }
  }
}

var groups = function(){
  this.findAll = function(options, callback){
    var url = apiBaseUrl + '/domain/groups';
     api.newRequest().from(url)
     .addRequestOptions({qs:(options || {})})
     .getResource(function(error, resource){
        if(error)
          return callback(error);
        else
          return callback(undefined, new LazyList(resource,Group, "groups"));
     });
  }

  this.findById = function(id, callback){
    var url = apiBaseUrl + "/domain/groups/" + id;
    api.newRequest().from(url).getResource(function(error, resource){
        if(error)
          return callback(error);
        else
          return callback(undefined, new Group(resource));
    });
  }


  this.findByDomain = function(options,id, callback){
    var url = apiBaseUrl + "/domain/groups/search/findByDomain";
    options = options || {};
    options.domain = id;
    api.newRequest().from(url)
      .addRequestOptions({qs:options})
      .getResource(function(error, resource){
        if(error)
          return callback(error);
        else
          return callback(undefined, new LazyList(resource,Group, "groups"));
    });
  }


 this.findByName = function(name, callback){
    var url = apiBaseUrl + '/domain/groups/search/findByDomainIdAndName';
    //console.log(name);
    //console.log(currentDomain.id);
    if (currentDomain === undefined || currentDomain.id === undefined){
      //console.log("No current domain set" );
      callback({message:"currentDomain is not set"});
    }else{

      api.newRequest().from(url)

      .addRequestOptions({qs:{ domain : currentDomain.id , name : name }})
      .getResource(function(error, resource){
         if(error)
           return callback(error);
         else
           return callback(undefined, new Group(resource));
      });
    }
  }


  this.findById = function(id, callback){
    var url = apiBaseUrl + "/domain/groups/" + id;
    api.newRequest().from(url).getResource(function(error, resource){
        if(error)
          return callback(error);
        else
          return callback(undefined, new Group(resource));
    });
  }
	
 this.find = function(options,callback){
	 console.log("Current Domain ==> " + currentDomain.id)
	  if(currentDomain && currentDomain.id){
		return this.findByDomain(options, currentDomain.id,callback);
	  }else{
		callback();
	  }
  }
}





var fields = function(){
  this.findAll = function(options, callback){
    var url = apiBaseUrl + '/domain/fields';
     api.newRequest().from(url)
     .addRequestOptions({qs:(options || {})})
     .getResource(function(error, resource){
        if(error)
          return callback(error);
        else
          return callback(undefined, new LazyList(resource,Field, "fields"));
     });
  }

  this.findById = function(id, callback){
    var url = apiBaseUrl + "/domain/fields/" + id;
    api.newRequest().from(url).getResource(function(error, resource){
        if(error)
          return callback(error);
        else
          return callback(undefined, new Field(resource));
    });
  }


  this.findByDomain = function(options,id, callback){
    var url = apiBaseUrl + "/domain/fields/search/findByDomain";
    options = options || {};
    options.domain = id;
    api.newRequest().from(url)
      .addRequestOptions({qs:options})
      .getResource(function(error, resource){
        if(error)
          return callback(error);
        else
          return callback(undefined, new LazyList(resource,Field, "fields"));
    });
  }


 this.findByName = function(name, callback){
    var url = apiBaseUrl + '/domain/fields/search/findByDomainIdAndName';
    //console.log(name);
    //console.log(currentDomain.id);
    if (currentDomain === undefined || currentDomain.id === undefined){
      //console.log("No current domain set" );
      callback({message:"currentDomain is not set"});
    }else{

      api.newRequest().from(url)

      .addRequestOptions({qs:{ domain : currentDomain.id , name : name }})
      .getResource(function(error, resource){
         if(error)
           return callback(error);
         else
           return callback(undefined, new Field(resource));
      });
    }

  }


  this.findById = function(id, callback){
    var url = apiBaseUrl + "/domain/fields/" + id;
    api.newRequest().from(url).getResource(function(error, resource){
        if(error)
          return callback(error);
        else
          return callback(undefined, new Field(resource));
    });
  }

   this.find = function(options,callback){
		  if(currentDomain && currentDomain.id){
			return this.findByDomain(options, currentDomain.id,callback);
		  }else{
			callback();
		  }
	}
}


var accessCodes = function(){

  this.findAll = function(options, callback){
    var url = apiBaseUrl + '/domain/domainAccessCodes';
     api.newRequest().from(url)
     .addRequestOptions({qs:(options || {})})
     .getResource(function(error, resource){
       //console.log(resource);
        if(error)
          return callback(error);
        else
          return callback(undefined, new LazyList(resource,AccessCode, "domainAccessCodes"));
     });
  }

  this.findById = function(id, callback){
    var url = apiBaseUrl + "/domain/domainAccessCodes/" + id;
    api.newRequest().from(url).getResource(function(error, resource){
        if(error)
          return callback(error);
        else
          return callback(undefined, new AccessCode(resource));
    });
  }




}


var umaas = (function () {
    var ret = {};
    ret.auth = {id: '0000', code: '0000'};
    ret.utils = utils;
    ret.domain = {};

    // object creators
    ret.Domain = Domain;
    ret.AppUser = AppUser;
    ret.Group = Group;
    ret.Role = Role;
    ret.AccessCode = AccessCode;
    ret.LazyList = LazyList
    ret.Field = Field;

    // entity managers
    ret.domains = new domains();
    ret.appUsers = new appUsers();
    ret.roles = new roles();
    ret.groups = new groups();
    ret.fields = new fields();
    ret.accessCodes = new accessCodes();

    //configuration
    ret.setDomain = setDomain;
    ret.getDomain = getDomain;
    ret.setBaseUrl = setBaseUrl;
    ret.getBaseUrl = getBaseUrl;
    ret.setAccessCode = setAccessCode;
    ret.getAccessCode = getAccessCode;

    return ret;
}());

// export api
try{
  if(window){
   window.umaas = umaas;
  }else{
    module.exports = umaas;
  }
}catch(e){
  module.exports = umaas;
}