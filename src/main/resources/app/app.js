var XSRF_COOKIE_NAME = "XSRF-AUTHN-TOKEN";
var application = angular.module('AuthenticationApp',
    ['ui.router', 'authentication.controllers', 'authentication.services','authentication.factories','toaster', 'ngSanitize', 'ngCookies','trNgGrid'])

application.config(['$stateProvider', '$urlRouterProvider','$httpProvider', function($stateProvider, $urlRouterProvider, $httpProvider) {
    $urlRouterProvider.when("", "/authentication/start");
    $urlRouterProvider.otherwise("/authentication/start");
    $httpProvider.defaults.xsrfCookieName=XSRF_COOKIE_NAME;
    $stateProvider
        .state('start', {
            abstract: false,
            url: '/authentication/start',
            templateUrl: '/views/login/login.html',
            controller: 'loginController'
        }).state('audits', {
            abstract: false,
            url: '/audits/start',
            templateUrl: '/views/audits/audits.html',
            controller: 'auditsController'
        })
}]);

application.directive('autofocus', ['$timeout', function($timeout) {
	  return {
		    restrict: 'A',
		    link : function($scope, $element) {
		      $timeout(function() {
		        $element[0].focus();
		      });
		    }
		  }
		}]);

application.run(['$http', '$cookies', function($http, $cookies) {
	$http.defaults.headers.post['X-XSRF-TOKEN'] = $cookies.get(XSRF_COOKIE_NAME);
}]);

