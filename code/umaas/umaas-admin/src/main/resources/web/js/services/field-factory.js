var getInputType = function(type){
        if(type === "string"){
            return "text";
        }else if(type === "Date"){
            return "date";
        }else if (type === "Integer" || type ==="Number" || type ==="Decimal"){
            return "number";
        }else if(type === "boolean"){
            return "checkbox";
        }else if (type === "Email"){
            return "email";
        }
        return "text";
    };
    var getFieldType = function(type){
        if(type === "boolean")
            return "checkbox";
        else if(type === "file")
            return "file-input";
        else if (type === "select")
            return "select";
        else if ( type === "date")
        	return "datepicker"
        else
            return "input";
    };
	var convertToDate = function(date){
		console.log("Converting date " + date)
		return new Date(date);
	}
	
var getFieldTemplate = function(field){
    var ret = {
                 "ngModelElAttrs": {
                    "ng-model": "model['properties']['".concat(field.name).concat("']")
                  },
                type: getFieldType(field.type),
                templateOptions: {
                    type: getInputType(field.type),
                    label: field.name,
                    required: field.mandatory
                }
            };
 if(field.type === "boolean"){
     delete ret.templateOptions.type;
     delete ret.templateOptions.required;
 }else  if(field.type === "file"){
	    delete ret.templateOptions.type;
		ret.key = field.id;
		ret.templateOptions.accept = field.properties.accept;
 }else  if(field.type === "select"){
	 var options = field.properties.options;
	var labels = field.properties.labels;
	var fieldOptions = [];
	if(!labels){
		labels = options;
	}
	for(var i=0; i< options.length; ++i){
		fieldOptions.push({label: (labels[i] || options[i]), value: options[i]});
	}
	ret.templateOptions.multiple = field.properties.multiple;
	ret.templateOptions.labelProp = "label";
	ret.templateOptions.valueProp = "value";
	ret.templateOptions.options = fieldOptions;
 }else if(field.type === "email"){
     ret.templateOptions.type = "email";
 }else  if (field.type === 'integer') {
	    ret.templateOptions.type = 'number';
	    ret.templateOptions.placeholder = field.properties.label || field.name;
		ret.templateOptions.min = field.properties.minimum;
		ret.templateOptions.max = field.properties.maximum;
 }else if (field.type === 'date'){
		ret.templateOptions.minDate = new Date(field.properties.minimum);
		ret.templateOptions.maxDate = new Date(field.properties.maximum);
		ret.formatters = [convertToDate];
 }else if (field.type === 'string'){
     ret.templateOptions.pattern = field.properties.pattern;

 }
    return ret;
};

angular.module('app')

.service("EntityFormFieldsFactory", function(DomainConstants,$q,
		umaas, $timeout, loader, userFields, groupFields,
		roleFields, customFieldFields){
    var self = this;
    var createMap = {};
    
    createMap[DomainConstants.Entity.User] = function(isNew){
    	console.log("Getting user fields");
    	var deferred = $q.defer();
    	var domain = umaas.getDomain();
    	loader.loadFields().then( function(fields){
    		loader.loadGroups().then(function(groups){
    			var fs = angular.copy(userFields);
    		    for(var i=0; i<fs.length; ++i){
    		        if(fs[i].key === 'username'){
    		           fs[i].hideExpression = 
    		                   domain.properties.emailAsUsername? 'true':'false';
    		        }
    		        if(fs[i].key === 'groups'){
    		        	fs[i].templateOptions.options = groups;
    		        	fs[i].templateOptions.key = 'name';
    		        }
    		    }
    		    for(var i=0; i<fields.length; ++i){
    		       fs.push(getFieldTemplate(fields[i]));
    		   }
    		    console.log("User fields gotten");
    		    console.log(fs);
    		   deferred.resolve(fs);
    		});
    	});
    	return deferred.promise;
    };
    var returnCopy = function(fieldTemplates){
    	var deferred = $q.defer();
    	deferred.resolve(angular.copy(fieldTemplates))
    	return deferred.promise;
    }
    createMap[DomainConstants.Entity.Group] = function(isNew){
    	var deferred = $q.defer();
    	var fields = angular.copy(groupFields)
    	if(isNew){
    		fields.pop();
    	}
    	deferred.resolve(fields);
    	return deferred.promise;
    };
    createMap[DomainConstants.Entity.Role] = function(isNew){
    	var deferred = $q.defer();
    	var fields = angular.copy(roleFields)
    	if(isNew){
    		fields.pop();
    	}
    	deferred.resolve(fields);
    	return deferred.promise;
    };
    createMap[DomainConstants.Entity.Field] = function(isNew){
    	var deferred = $q.defer();
    	var fs = angular.copy(customFieldFields);
    	var defaultValueField;
    	deferred.resolve(fs);
    	return deferred.promise;
    };
    console.log(createMap);
    self.create = function(entityType, isNew){
    	 return createMap[entityType](isNew);
    };
    
});
