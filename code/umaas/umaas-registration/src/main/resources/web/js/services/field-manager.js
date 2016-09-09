var defaultFields = function(){
	return [ {
	      key: 'username',
	      type: 'input',
	      templateOptions: {
	        type: 'text',
	        label: 'Username',
	        placeholder: 'Enter username',
	        required: true
	      }
	    },{
	      key: 'email',
	      type: 'input',
	      templateOptions: {
	        type: 'email',
	        label: 'Email address',
	        placeholder: 'Enter email',
	        required: true
	      }
	    },
	    {
		      key: 'phoneNumber',
		      type: 'input',
		      templateOptions: {
		        type: 'text',
		        label: 'Phone Number',
		        placeholder: 'Enter Phone Number',
		        required: true
		      }
		 },
	    {
	      key: 'password',
	      type: 'input',
	      templateOptions: {
	        type: 'password',
	        label: 'Password',
	        placeholder: 'Password',
	        required: true
	      }
	    },
	    {
          type: "input",
          name: "confirmPassword",
          validators: {
                matchPassword: {
                    expression: "$viewValue === model.password",
                    message: '"Password does not match"'
                }
            },
            templateOptions: {
                "type": "password",
                "required": true,
                "label": "Confirm Password",
                "placeholder": "Confirm Password"
            }
        }];
}

var makeField = function(fieldData){
	var field = {};
	console.log(fieldData);
	field.ngModelElAttrs ={
        "ng-model": "model['properties']['".concat(fieldData.name).concat("']")
      };
	field.type = 'input';
	field.templateOptions = {
	        type: 'text',
	        label: (fieldData.properties.label || fieldData.name),
	        placeholder: (fieldData.properties.placeholder || fieldData.name),
	        required: fieldData.mandatory,
	        pattern: fieldData.properties.pattern
	}
	
		if (fieldData.type === 'integer') {
			field.templateOptions.type = 'number';
			field.templateOptions.min = fieldData.properties.minimum;
			field.templateOptions.max = fieldData.properties.maximum;
		}
		else if (fieldData.type === 'email') {
			field.templateOptions.type = 'email';
		}
		else if (fieldData.type === 'date'){
			field.type = 'datepicker';
			field.templateOptions.minDate = new Date(fieldData.properties.minimum);
			field.templateOptions.maxDate = new Date(fieldData.properties.maximum);
		}
		else if (fieldData.type === 'select'){
			field.type = 'select';
			var options = fieldData.properties.options;
			var labels = fieldData.properties.labels;
			var fieldOptions = [];
			if(!labels){
				labels = options;
			}
			for(var i=0; i< options.length; ++i){
				
				fieldOptions.push({label: (labels[i] || options[i]), value: options[i]});
			}
			field.templateOptions.multiple = fieldData.properties.multiple;
			field.templateOptions.labelProp = "label";
			field.templateOptions.valueProp = "value";
			field.templateOptions.options = fieldOptions;
			
		}
		else if(fieldData.type === 'file'){
			field.key = fieldData.id;
			field.type = "file-input";
			field.templateOptions.accept = fieldData.properties.accept;
		}
	
	return field;
}

angular.module('app')

.service('fieldManager', function(umaas, $rootScope, $q){
	var fieldLoaded = false;
	var umaasFields;
	var customFields;
	console.log("initializing custom fields");
	var getCustomFields = function(){
		return umaasFields;
	}
	this.getFields = function(){
		var deferred = $q.defer();
		var fields = defaultFields();
		if(fieldLoaded){
			var customFields = getCustomFields();
			fields = fields.concat(customFields);
			deferred.resolve(fields);
		}else{
			umaas.fields.find({size:30}, function(error, fs){
				if(!error){ 
					console.log("Found Fields");
					umaasFields = [];
					customFields = = fs.content;
					fs.content.forEach(function(field){
						if(field.registrationItem){
							umaasFields.push(makeField(field));		
						}
					})
					fieldLoaded = true;
					fields = fields.concat(umaasFields);
					deferred.resolve(fields);
				}
				else throw error;
			})
		}
		return deferred.promise;
	}
});

