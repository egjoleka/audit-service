angular.module('authentication.controllers').controller('auditsController', ['auditsService', '$location', '$scope', function(resetPasswordService, $location, $scope) {
	   function init() {
		   $scope.isValidRequest = true;
	   }
	   init();
	   
	   $scope.getAudits = function(bearer, flag) {
		   auditsService.getAudits(bearer, flag).then(function(data) {
			   console.log(data);
		   });
	   }

	   function isBlank(str) {
		   return !!(str || '').match(/^\s*$/);
	   }
}]);