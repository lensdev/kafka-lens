(function() {
    'use strict';

    angular
        .module('kafkalensApp')
        .factory('KafkaeventSearch', KafkaeventSearch);

    KafkaeventSearch.$inject = ['$resource'];

    function KafkaeventSearch($resource) {
        var resourceUrl =  'api/_search/kafkaevents/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
