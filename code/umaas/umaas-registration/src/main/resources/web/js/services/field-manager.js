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

angular.module('app')

.service('fieldManager', function(umaas){
	var fieldLoaded = false;
	var umaasFields;
	umaas.fields.find({size:30}, function(fields){
		umaasFields = fields;
		fieldLoaded = true;
		console.log(fields);
	})
	
	this.getFields = function(){
		return defaultFields();
	}
});
