angular.module('authentication.services').factory('loginService', function($http,$location, $q, toaster) {
   
    return {
	 	login: function(user, password) {
    		var deferred = $q.defer();
    		var data = {
    				user:user
    		}
    		var encodedUsername = encodeURIComponent(user);
    		var encodedPassword = encodeURIComponent(password);
 
			$http({
					url: '/auth/verification',
					method: 'POST',
					data: 'user=' + encodedUsername +"&password=" + encodedPassword,
					headers: {
						"Content-Type": "application/x-www-form-urlencoded"
					}
			}).success(function(response){
				deferred.resolve(response);
				
			}).error(function(data, status){
				deferred.resolve(data);
			});
			return deferred.promise;
		}
    }});