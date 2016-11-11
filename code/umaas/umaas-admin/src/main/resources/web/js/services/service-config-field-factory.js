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
    self.create = function(specs){
    	if(!specs) throw "specifications is undefined";
    	var fields = []
    	Object.keys(specs).forEach(function(key){
    		var field =  {
                 key: key,
                 type: 'input',
                 templateOptions: {
                     type: getInputType(specs[key]),
                     label: key,
                     required: false
                 }
             }
    		fields.push(field);
    	});	
    	
    	return fields;
    };
    
    
});
