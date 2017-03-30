(function() {
    'use strict';

    angular
        .module('kafkalensApp')
        .controller('KafkaeventDetailController', KafkaeventDetailController);

    KafkaeventDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Kafkaevent'];

    function KafkaeventDetailController($scope, $rootScope, $stateParams, previousState, entity, Kafkaevent) {
        var vm = this;

        vm.kafkaevent = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('kafkalensApp:kafkaeventUpdate', function(event, result) {
            vm.kafkaevent = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
