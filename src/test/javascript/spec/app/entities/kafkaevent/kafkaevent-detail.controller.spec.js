'use strict';

describe('Controller Tests', function() {

    describe('Kafkaevent Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockPreviousState, MockKafkaevent;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockPreviousState = jasmine.createSpy('MockPreviousState');
            MockKafkaevent = jasmine.createSpy('MockKafkaevent');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity,
                'previousState': MockPreviousState,
                'Kafkaevent': MockKafkaevent
            };
            createController = function() {
                $injector.get('$controller')("KafkaeventDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'kafkalensApp:kafkaeventUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
