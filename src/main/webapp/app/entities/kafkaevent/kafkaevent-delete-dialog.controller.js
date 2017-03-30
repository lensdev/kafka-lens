(function() {
    'use strict';

    angular
        .module('kafkalensApp')
        .controller('KafkaeventDeleteController',KafkaeventDeleteController);

    KafkaeventDeleteController.$inject = ['$uibModalInstance', 'entity', 'Kafkaevent'];

    function KafkaeventDeleteController($uibModalInstance, entity, Kafkaevent) {
        var vm = this;

        vm.kafkaevent = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Kafkaevent.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
