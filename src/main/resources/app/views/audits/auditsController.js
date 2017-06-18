angular.module('authentication.controllers').controller('auditsController', ['auditsService', '$location', '$scope', function(auditsService, $location, $scope) {
	   function init() {
		   $scope.isValidRequest = true;
		   $scope.bearer = $location.search().bearer;
		   $scope.isSuccess = $location.search().isSuccess;
		   
		   console.log($scope.bearer)
		   auditsService.getAudits($scope.bearer, $scope.isSuccess).then(function(data) {
			   $scope.audits = data.loginAudits;
		   });
	   }
	   init();
	   function isBlank(str) {
		   return !!(str || '').match(/^\s*$/);
	   }
}]);