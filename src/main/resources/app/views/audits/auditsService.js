angular.module('authentication.services').factory('auditService', function($http, $q, toaster) {
   
    return {
    	getAudits: function(bearer, flag) {
    		var deferred = $q.defer();
    		$http.get("/audits?bearer=" + code + "isSuccess=" + flag).success(function(response){
    			deferred.resolve(response);
    		}).error(function(data, status){
    			deferred.resolve(data);
    			toaster.error("Error occurred!")
    		});
    		return deferred.promise;
    	},
    }});