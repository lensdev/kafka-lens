(function() {
    'use strict';

    angular
        .module('kafkalensApp')
        .controller('KafkaeventDialogController', KafkaeventDialogController);

    KafkaeventDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Kafkaevent'];

    function KafkaeventDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Kafkaevent) {
        var vm = this;

        vm.kafkaevent = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.kafkaevent.id !== null) {
                Kafkaevent.update(vm.kafkaevent, onSaveSuccess, onSaveError);
            } else {
                Kafkaevent.save(vm.kafkaevent, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('kafkalensApp:kafkaeventUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.eventtime = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
