angular.module('app')

.service("serviceConfigFieldFactory", function(){
    var self = this;
    var getInputType = function (type){
    	switch(type){
    	case "Integer": case "Float": case "Long": case "Double": case "BigInteger": case "BigDecimal": return "number";
    	case "Date": return "date";
    	default: return "text"
    	}
    }
    var modifyField = function(field, spec){
    	if(spec.type === "Boolean"){
			field.type = 'checkbox';
		}
    	if(spec.inputType === "textarea"){
    		field.type = 'textarea';
    		field.templateOptions.rows = 5;
    	}
    }
    self.create = function(specs){
    	if(!specs) throw "specifications is undefined";
    	var fields = []
    	specs.forEach(function(spec){
    		var field =  {
                 key: spec.key,
                 type: 'input',
                 templateOptions: {
                     type: getInputType(spec.type),
                     label: spec.name,
                     required: false
                 }
             }
    		modifyField(field, spec);
    		fields.push(field);
    	});	
    	
    	return fields;
    };
    
    
});
