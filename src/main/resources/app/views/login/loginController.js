angular.module('authentication.controllers').controller('loginController',['loginService', '$location', '$scope',  'toaster','$window', '$cookies', function(loginService, $location, $scope,toaster, $window, $cookies) {
   
   
   function init() {
	   $scope.showUsernameDiv = true;
	   $scope.username = "";
	   $scope.password = "";
	   $scope.isValidRequest = true;
	   $scope.forgotPwdLink = '#/passwords?clientApplicationId=' + $scope.clientId + '&applicationUri=' + $scope.redirectURI;
	   $cookies.remove(XSRF_COOKIE_NAME);
   }
  
   init();
   
   $scope.showPassword = function(username) { 
	   $scope.showUsernameDiv = false;
	   $scope.username = username;
	   
   }
   
   $scope.login = function(username, password) {
	   loginService.login(username, password).then(function(data) {
		   
		   if (data.error_description) {
			   $scope.error_description = data.error_description;
		   } else {
			   $scope.bearerValue = data.bearerToken;
			   $scope.success = true;
		   }
	   })
   }
   
   $scope.backToUsernameView = function() {
	   $scope.showUsernameDiv = true;
   }

}]);
