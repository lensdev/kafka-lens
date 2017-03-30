(function() {
    'use strict';
    angular
        .module('kafkalensApp')
        .factory('Kafkaevent', Kafkaevent);

    Kafkaevent.$inject = ['$resource', 'DateUtils'];

    function Kafkaevent ($resource, DateUtils) {
        var resourceUrl =  'api/kafkaevents/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.eventtime = DateUtils.convertLocalDateFromServer(data.eventtime);
                    }
                    return data;
                }
            },
            'update': {
                method: 'PUT',
                transformRequest: function (data) {
                    var copy = angular.copy(data);
                    copy.eventtime = DateUtils.convertLocalDateToServer(copy.eventtime);
                    return angular.toJson(copy);
                }
            },
            'save': {
                method: 'POST',
                transformRequest: function (data) {
                    var copy = angular.copy(data);
                    copy.eventtime = DateUtils.convertLocalDateToServer(copy.eventtime);
                    return angular.toJson(copy);
                }
            }
        });
    }
})();
