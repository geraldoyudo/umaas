var defaultFields = function(domain){
	return [ {
	      key: 'username',
	      type: 'input',
	      hideExpression: domain.properties.emailAsUsername? 'true':'false',
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

var makeVerificationField = function(field){
	var verifier = field.properties.verifier;
	if(!verifier || (verifier.type !== 'TWO_STEP')){
		return;
	}

	var vField = {
  	      type: 'verify-item',
  	      name: field.id,
  	      templateOptions: {
  	    	verifier: verifier.name ,
  	        label: (field.properties.label || field.name),
  	        required: true
  	      }
	    };
	
	vField.templateOptions.key = "model['properties']['".concat(field.name).concat("']");
	return vField;
	
}

angular.module('app')

.service('fieldManager', function(umaas, $rootScope, $q){
	var fieldLoaded = false;
	var umaasFields;
	var customFields;
	var vFields;
	console.log("initializing custom fields");
	var getCustomFields = function(){
		return umaasFields;
	}
	var getVFields = function(){
		var vfs = [];
		var domain = umaas.getDomain();
		if(domain.domainProperties.verifyEmail){
			vfs.push({
		        	 key: 'email',
			   	      type: 'verify-item',
			   	      name: 'email',
			   	      templateOptions: {
			   	    	verifier: 'email',
			   	        label: 'Email address',
			   	        placeholder: 'Enter email',
			   	        required: true
			   	      }
		   	    });
		}
		if(domain.domainProperties.verifyPhoneNumber){
			vfs.push( {
	        	 key: 'phoneNumber',
		   	      type: 'verify-item',
		   	      name: 'phoneNumber',
		   	      templateOptions: {
		   	    	verifier: 'phone',
		   	        label: 'Phone Number',
		   	        required: true
		   	      }
	   	    });
		}
		var valField;
		for(var i=0; i< customFields.length; ++i){
			valField = makeVerificationField(customFields[i]);
			if(valField){
				vfs.push(valField);
			}
		}
		return vfs;
	}
	this.getVerificationFields = function(){
		return vFields;
	}
	this.getFields = function(){
		var deferred = $q.defer();
		var fields = defaultFields(umaas.getDomain());
		if(fieldLoaded){
			customFields = getCustomFields();
			fields = fields.concat(customFields);
			deferred.resolve(fields);
		}else{
			umaas.fields.find({size:30}, function(error, fs){
				if(!error){ 
					console.log("Found Fields");
					umaasFields = [];
					customFields = fs.content;
					fs.content.forEach(function(field){
						if(field.registrationItem){
							umaasFields.push(makeField(field));		
						}
					})
					fieldLoaded = true;
					fields = fields.concat(umaasFields);
					 vFields = getVFields();
					console.log(vFields);
					deferred.resolve(fields);
				}
				else throw error;
			})
		}
		return deferred.promise;
	}
});

