angular.module('app')

.run(function(formlyConfig){
	formlyConfig.setType({
	  name: 'file-input',
	  templateUrl: '/app/partials/templates/file-input.htm'
	});
}
);

